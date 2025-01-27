import argparse
import json
import os
import utils.constants as c
from utils.requester import Requester
import requests
from bs4 import BeautifulSoup

def get_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument('output_file', type=str)
    parser.add_argument('-start_page', type=int, default=1)
    return parser.parse_args()

#Scraping del prezzo dell'alcol e della descrizione HTML
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
    description = None

    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')

        #Estrazione del prezzo
        price_span = soup.find('span', class_='purchaseAvailability__currentPrice--3mO4u')
        if price_span:
            price = price_span.get_text(strip=True)

        #Estrazione della percentuale alcolica e della descrizione
        try:
            facts = soup.find_all('td', class_='wineFacts__fact--3BAsi')

            #Percentuale alcolica: primo elemento con "%":
            for fact in facts:
                text = fact.get_text(strip=True)
                if "%" in text:
                    alcohol_percentage = text
                    break

            #Controllo sul numero di elementi
            if len(facts) == 7:
                description = facts[-1].get_text(strip=True)
            else:
                description = None

        except Exception as e:
            print(f"Errore durante il parsing della percentuale alcolica o della descrizione: {e} (link: {wine_url})")

    return {
        'price': price,
        'alcohol_percentage': alcohol_percentage,
        'description': description
    }



if __name__ == '__main__':
    args = get_arguments()
    output_dir = args.output_file
    start_page = args.start_page

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    r = Requester(c.BASE_URL)

    payload = {
        "country_codes[]": "it",
        "min_rating": 1.0
    }

    res = r.get('explore/explore?', params=payload)
    n_matches = res.json()['explore_vintage']['records_matched']
    print(f'Numero totale di vini ottenuti: {n_matches}')

    for i in range(start_page, max(1, int(n_matches / c.RECORDS_PER_PAGE)) + 1):
        data = {'wines': []}
        payload['page'] = i
        print(f'Page: {payload["page"]}')
        res = r.get('explore/explore', params=payload)
        matches = res.json()['explore_vintage']['matches']

        for match in matches:
            wine = match['vintage']['wine']

            try:
                price_info = match['vintage'].get('price', None)

                wine_html_url = f"https://www.vivino.com/w/{wine['id']}"
                html_data = get_html_data(wine_html_url)

                if price_info:
                    wine['price'] = price_info
                else:
                    wine['price'] = html_data['price']

                wine['alcohol_percentage'] = html_data.get('alcohol_percentage', None)
                wine['description'] = html_data.get('description', None)
                wine['country'] = 'Italy'

                print(f"Scraping data: {wine['name']}, country: {wine['country']}, price: {wine['price']}, alcol: {wine.get('alcohol_percentage', 'N/A')}, description: {wine.get('description', 'N/A')} (link: {wine_html_url})")

                data['wines'].append(wine)

                res_taste = r.get(f'wines/{wine["id"]}/tastes')
                tastes = res_taste.json()
                data['wines'][-1]['taste'] = tastes.get('tastes', [])

                all_reviews = []
                page_reviews = 1
                while True:
                    res_review = r.get(f'wines/{wine["id"]}/reviews', params={"page": page_reviews})
                    reviews_data = res_review.json()
                    current_reviews = reviews_data.get('reviews', [])
                    
                    if not current_reviews or page_reviews > 15:
                        break
                    all_reviews.extend(current_reviews)
                    page_reviews += 1

                data['wines'][-1]['reviews'] = all_reviews

            except KeyError as e:
                print(f'Errore: {e}. Saltando il vino {wine["name"]}.')

        filename = os.path.join(output_dir, f"data_{i}.json")
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False)
