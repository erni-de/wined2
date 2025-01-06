import json
import uuid
import random

# Funzione per generare un UUID
def generate_unique_id():
    return str(uuid.uuid4())

# Funzione per generare un prezzo casuale tra 10 e 100
def generate_random_price():
    return random.randint(10, 100)

# Leggi il file JSON originale
with open('wines.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Aggiorna i documenti
for wine in data:
    if wine.get('provenance') == 'V':
        # Rimuovi il simbolo dell'euro (€) e i separatori di migliaia dal prezzo
        if 'price' in wine and isinstance(wine['price'], str):
            cleaned_price = wine['price'].replace('€', '').replace(',', '').strip()
            wine['price'] = int(cleaned_price) if cleaned_price.isdigit() else generate_random_price()
        else:
            wine['price'] = generate_random_price()
    elif wine.get('provenance') == 'W':
        # Se non esiste un wine_id, lo creiamo
        if 'id' not in wine:
            wine['id'] = generate_unique_id()

        # Aggiorna la struttura della winery per Winemag
        if 'winery' in wine:
            # Se winery non è un dizionario, la creiamo come tale
            if not isinstance(wine['winery'], dict):
                wine['winery'] = {
                    'id': generate_unique_id(),
                    'name': wine['winery'] if isinstance(wine['winery'], str) else 'Unknown'
                }
            else:
                # Se manca l'ID o il nome, li aggiungiamo
                if 'id' not in wine['winery']:
                    wine['winery']['id'] = generate_unique_id()
                if 'name' not in wine['winery']:
                    wine['winery']['name'] = 'Unknown'
        else:
            # Crea una nuova struttura winery se non esiste
            wine['winery'] = {
                'id': generate_unique_id(),
                'name': 'Unknown'
            }

        # Se il prezzo non è disponibile, assegniamo un valore casuale
        if 'price' not in wine or wine['price'] is None:
            wine['price'] = generate_random_price()

# Salva i dati aggiornati in un nuovo file JSON senza codifica Unicode
with open('updated_wines.json', 'w', encoding='utf-8') as file:
    json.dump(data, file, indent=4, ensure_ascii=False)

# Leggi il file JSON
with open('updated_wines.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Conta i vini per provenienza
num_winemag = sum(1 for wine in data if wine.get('provenance') == 'W')
num_vivino = sum(1 for wine in data if wine.get('provenance') == 'V')

# Stampa i risultati
print(f"Numero di vini di Winemag: {num_winemag}")
print(f"Numero di vini di Vivino: {num_vivino}")
