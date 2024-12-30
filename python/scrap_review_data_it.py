import argparse
import json
import os
import utils.constants as c
from utils.requester import Requester

def get_arguments():
    """Ottiene gli argomenti dalla linea di comando."""
    parser = argparse.ArgumentParser(description='Esegue lo scraping delle recensioni dei vini da Vivino e salva i risultati in una cartella come file JSON.')
    parser.add_argument('output_file', help='La cartella in cui verranno salvati i file .json di output', type=str)
    parser.add_argument('-start_page', help='Identificatore pagina di partenza', type=int, default=1)
    return parser.parse_args()

if __name__ == '__main__':
    args = get_arguments()
    output_dir = args.output_file
    start_page = args.start_page

    # Assicuriamoci che la cartella esista
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    r = Requester(c.BASE_URL)

    payload = {
        "country_codes[]": "it",
        "min_rating": 1.0
    }

    try:
        # Ottenere il numero totale di vini
        res = r.get('explore/explore?', params=payload)
        n_matches = res.json().get('explore_vintage', {}).get('records_matched', 0)
        print(f'Numero totale di vini ottenuti: {n_matches}')
    except Exception as e:
        print(f'Errore durante l\'ottenimento del numero totale di vini: {e}')
        n_matches = 0

    # Iterare dalla pagina più alta alla più bassa
    for i in range(max(1, int(n_matches / c.RECORDS_PER_PAGE)), start_page - 1, -1):
        try:
            data = {'wines': []}
            payload['page'] = i
            print(f'Pagina attuale: {payload["page"]}')
            
            res = r.get('explore/explore', params=payload)
            matches = res.json().get('explore_vintage', {}).get('matches', [])

            for match in matches:
                wine = match.get('vintage', {}).get('wine', {})
                if not wine:
                    print('Dati del vino mancanti, salto.')
                    continue

                try:
                    # Paginazione delle recensioni
                    all_reviews = []
                    page_reviews = 1
                    while True:
                        try:
                            res_review = r.get(f'wines/{wine["id"]}/reviews', params={"page": page_reviews})
                            reviews_data = res_review.json()
                            current_reviews = reviews_data.get('reviews', [])

                            if not current_reviews or page_reviews > 300:
                                break
                            all_reviews.extend(current_reviews)
                            page_reviews += 1
                        except Exception as e:
                            print(f'Errore durante lo scraping delle recensioni per il vino {wine.get("name", "sconosciuto")}: {e}')
                            break

                    data['wines'].append({
                        'wine_id': wine['id'],
                        'wine_name': wine['name'],
                        'reviews': all_reviews
                    })

                    print(f"Recensioni scaricate per il vino: {wine['name']} ({len(all_reviews)} recensioni)")

                except Exception as e:
                    print(f'Errore durante l\'elaborazione del vino {wine.get("name", "sconosciuto")}: {e}')

            # Salvataggio dei dati della pagina
            filename = os.path.join(output_dir, f"data_{i}.json")
            try:
                with open(filename, 'w', encoding='utf-8') as f:
                    json.dump(data, f, ensure_ascii=False)
                print(f'Dati salvati per la pagina {i} in {filename}')
            except Exception as e:
                print(f'Errore durante il salvataggio dei dati per la pagina {i}: {e}')

        except Exception as e:
            print(f'Errore durante l\'elaborazione della pagina {i}: {e}')
