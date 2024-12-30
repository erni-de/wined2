import argparse
import json
import os
import utils.constants as c
from utils.requester import Requester
import requests
from bs4 import BeautifulSoup

def get_arguments():
    """Ottiene gli argomenti dalla linea di comando."""
    parser = argparse.ArgumentParser(description='Esegue lo scraping di tutti i dati sui vini da Vivino e salva i risultati in una cartella come file JSON.')
    parser.add_argument('output_file', help='La cartella in cui verranno salvati i file .json di output', type=str)
    parser.add_argument('-start_page', help='Identificatore pagina di partenza', type=int, default=1)
    return parser.parse_args()

#Funzione per fare scraping del prezzo e dell'alcol HTML
def get_html_data(wine_url):
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                      " AppleWebKit/537.36 (KHTML, like Gecko)"
                      " Chrome/70.0.3538.77 Safari/537.36",
        "Accept-Language": "en-US,en;q=0.9"
    }

    response = requests.get(wine_url, headers=headers)
    price = None
    alcohol_percentage = None

    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')

        #Estrazione del prezzo
        price_span = soup.find('span', class_='purchaseAvailability__currentPrice--3mO4u')
        if price_span:
            price = price_span.get_text(strip=True)

        try:
            #Con la find normale non funzionava, ho più tag con la stessa classe quindi ho provato la findall
            facts = soup.find_all('td', class_='wineFacts__fact--3BAsi')
            for fact in facts:
                #Il simbolo % è presente in ogni gradazione e mi permette di individuarlo
                if "%" in fact.get_text():
                    alcohol_percentage = fact.get_text(strip=True)
                    break
        except Exception as e:
            print(f"Errore durante il parsing della percentuale alcolica: {e}")

    return {
        'price': price,
        'alcohol_percentage': alcohol_percentage
    }

if __name__ == '__main__':
    args = get_arguments()
    output_dir = args.output_file
    start_page = args.start_page

    #Assicuriamoci che la cartella esista
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    r = Requester(c.BASE_URL)

    payload = {
        "country_codes[]": "it",
        "min_rating": 1.0
    }

    #Ottenere il numero totale di vini
    res = r.get('explore/explore?', params=payload)
    n_matches = res.json()['explore_vintage']['records_matched']
    print(f'Numero totale di vini ottenuti: {n_matches}')

    # Iterare su tutte le pagine
    for i in range(start_page, max(1, int(n_matches / c.RECORDS_PER_PAGE)) + 1):
        data = {'wines': []}
        payload['page'] = i
        print(f'Page: {payload["page"]}')
        res = r.get('explore/explore', params=payload)
        matches = res.json()['explore_vintage']['matches']

        for match in matches:
            wine = match['vintage']['wine']
            
            try:
                # Provo a cercare il prezzo nella risposta dell'API
                price_info = match['vintage'].get('price', None)

                # Anche se ho già il prezzo, provo a fare uno scraping HTML per la percentuale alcolica
                wine_html_url = f"https://www.vivino.com/w/{wine['id']}"
                html_data = get_html_data(wine_html_url)

                if price_info:
                    wine['price'] = price_info
                else:
                    # Se non c'è prezzo dall'API, uso quello HTML
                    if html_data['price']:
                        wine['price'] = html_data['price']
                    else:
                        # Se non trovo neanche quello HTML, sollevo errore ma continuo senza interrompere il programma
                        raise KeyError("Prezzo non disponibile per il vino.")

                # Se presente, aggiungo la percentuale alcolica, altrimenti proseguo senza bloccare il programma
                if html_data['alcohol_percentage']:
                    wine['alcohol_percentage'] = html_data['alcohol_percentage']
                else:
                    wine['alcohol_percentage'] = None  #Nessun valore disponibile

                print(f'Scraping data from wine: {wine["name"]}, price: {wine["price"]}, alcol: {wine.get("alcohol_percentage", "N/A")}, link: https://www.vivino.com/w/{wine["id"]}')
                data['wines'].append(wine)

                # Richiesta delle caratteristiche organolettiche
                res_taste = r.get(f'wines/{wine["id"]}/tastes')
                tastes = res_taste.json()
                data['wines'][-1]['taste'] = tastes.get('tastes', [])

                # Paginazione delle recensioni
                all_reviews = []
                page_reviews = 1
                while True:
                    res_review = r.get(f'wines/{wine["id"]}/reviews', params={"page": page_reviews})
                    reviews_data = res_review.json()
                    current_reviews = reviews_data.get('reviews', [])
                    print("Recensioni prese")

                    if not current_reviews or page_reviews > 15:
                        break
                    all_reviews.extend(current_reviews)
                    page_reviews += 1
                    print("Sto scaricando la recensione numero " + str(page_reviews))

                data['wines'][-1]['reviews'] = all_reviews

            except KeyError as e:
                # Gestione dell'errore: se non c'è prezzo o qualche altro problema con i dati, continuiamo con il prossimo vino
                print(f'Errore: {e}. Saltando il vino {wine["name"]}.')

        # Salvataggio dei dati della pagina
        filename = os.path.join(output_dir, "data_" + str(i) + ".json")
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False)
