import json
import uuid
import random

# Funzione per generare un UUID
def generate_unique_id():
    return str(uuid.uuid4())

# Funzione per generare un prezzo casuale tra 10 e 100
def generate_random_price():
    return random.randint(10, 100)

# Funzione per generare una percentuale di alcol casuale tra 12% e 18%
def generate_random_alcohol_percentage():
    return str(random.randint(12, 18)) + '%'

# Leggi il file JSON originale
with open('updated_wines.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Aggiorna i documenti
for wine in data:
    if wine.get('provenance') == 'V':
        # Cambiamenti specifici per Vivino
        wine['variety'] = {
            1: "Red",
            2: "White",
            3: "Champagne"
        }.get(wine['type_id'], "Red")
        del wine['type_id']  # Rimuove type_id dopo averlo convertito in variety

        if 'region' in wine and 'name' in wine['region']:
            wine['region'] = wine['region']['name']  # Flattens the region structure

        if 'winery' in wine and 'status' in wine['winery']:
            del wine['winery']['status']  # Rimuove lo status dal dizionario winery

    elif wine.get('provenance') == 'W':
        # Cambiamenti specifici per Winemag
        if 'title' in wine:
            wine['name'] = wine['title']  # Rinomina title in name
            del wine['title']

        if 'region_1' in wine:
            wine['region'] = wine['region_1']
            del wine['region_1']

        if 'region_2' in wine:
            del wine['region_2']

        if 'taster_twitter_handle' in wine:
            del wine['taster_twitter_handle']

        if 'designation' in wine:
            del wine['designation']

# Salva i dati aggiornati in un nuovo file JSON senza codifica Unicode
with open('updated_wines_V2.json', 'w', encoding='utf-8') as file:
    json.dump(data, file, indent=4, ensure_ascii=False)

# Leggi il file JSON
with open('updated_wines_V2.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Conta i vini per provenienza
num_winemag = sum(1 for wine in data if wine.get('provenance') == 'W')
num_vivino = sum(1 for wine in data if wine.get('provenance') == 'V')

# Stampa i risultati
print(f"Numero di vini di Winemag: {num_winemag}")
print(f"Numero di vini di Vivino: {num_vivino}")
