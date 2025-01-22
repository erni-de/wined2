import os
import json
import uuid
import random

# ------------- Configurazioni di base -------------
# Cartelle di input:
DIRECTORIES = [
    "../../Data_Cleaning/vivino_wines/EN_FR/",
    "../../Data_Cleaning/vivino_wines/EN_IT/",
    "../../Data_Cleaning/vivino_wines/EN_US/"
]

# Numero massimo di vini da estrarre
MAX_WINES = 100000

# File di output finale
OUTPUT_FILE = "vivino_wines_final.json"

# Mappatura di type_id -> variety (esempio base, personalizzalo se hai mappature diverse)
TYPE_ID_TO_VARIETY = {
    1: "Red",
    2: "White",
    3: "Champagne",
    7: "White"  # Sauternes è tecnicamente un vino dolce bianco, se vuoi gestirlo così
}

# ------------- Funzioni di utilità -------------
def genera_uuid():
    """Genera un UUID in formato stringa."""
    return str(uuid.uuid4())

def pulisci_prezzo(raw_price):
    """
    Rimuove simboli come '€', virgole, spazi ecc.
    Prova a convertire in int.
    Se non riesce, restituisce un prezzo casuale tra 10 e 100.
    """
    if not raw_price or not isinstance(raw_price, str):
        return random.randint(10, 100)
    # Rimuovi caratteri superflui
    cleaned = raw_price.replace("€", "").replace("$", "").replace(",", "").strip()
    # Se c'è un punto decimale, proviamo a gestirlo
    if "." in cleaned:
        # Togliamo la parte decimale (es: "14.99" -> 14)
        parts = cleaned.split(".")
        cleaned = parts[0]
    # Ora tentiamo la conversione
    try:
        return int(cleaned)
    except ValueError:
        # Se fallisce, random
        return random.randint(10, 100)

def pulisci_alcol(raw_alc):
    """
    Converte un valore tipo '14.8%' in un intero.
    Se fallisce, genera un valore random tra 12 e 18.
    """
    if not raw_alc or not isinstance(raw_alc, str):
        return random.randint(12, 18)
    cleaned = raw_alc.replace("%", "").strip()
    # Se c'è un punto, proviamo a prendere la parte intera
    if "." in cleaned:
        parts = cleaned.split(".")
        cleaned = parts[0]
    # Ora tentiamo la conversione
    try:
        return int(cleaned)
    except ValueError:
        return random.randint(12, 18)

def mappa_type_id_a_variety(type_id_value):
    """
    Ritorna la stringa variety a partire da un type_id.
    Se non mappato, restituisce "Red" di default.
    """
    return TYPE_ID_TO_VARIETY.get(type_id_value, "Red")

# ------------- Funzione di trasformazione -------------
def trasforma_vivino(doc_originale):
    """
    Trasforma un documento 'wine' (dal JSON di Vivino) 
    nello schema semplificato desiderato, 
    rimuovendo reviews, rigenerando ID, ecc.
    """
    # Genera nuovo _id e nuovo winery.id
    new_id = genera_uuid()
    winery_id = genera_uuid()

    # Ricava/correggi campi
    final_name = doc_originale.get("name", None)
    final_price = pulisci_prezzo(doc_originale.get("price"))
    final_alcohol = pulisci_alcol(doc_originale.get("alcohol_percentage"))
    final_description = doc_originale.get("description", None)
    final_country = doc_originale.get("country", None)
    
    # Se region è un dict con "name", allora estrai. Se è string, mantieni.
    region_field = doc_originale.get("region", {})
    if isinstance(region_field, dict):
        final_region = region_field.get("name", None)
    else:
        final_region = region_field

    # Gestione winery
    winery_data = doc_originale.get("winery", {})
    winery_name = winery_data.get("name", None)

    # Gestione taste
    taste_data = doc_originale.get("taste", {})
    structure_data = taste_data.get("structure", {})
    flavor_list = taste_data.get("flavor", [])

    # Compattiamo la struttura come richiesto
    final_taste_structure = {
        "acidity": structure_data.get("acidity"),
        "fizziness": structure_data.get("fizziness"),
        "intensity": structure_data.get("intensity"),
        "sweetness": structure_data.get("sweetness"),
        "tannin": structure_data.get("tannin")
    }

    # Creiamo la lista flavor nel formato { "group": ..., "mentions_count": ... }
    final_flavor = []
    if isinstance(flavor_list, list):
        for fobj in flavor_list:
            group_val = fobj.get("group")
            stats_val = fobj.get("stats", {})
            mentions_count = stats_val.get("mentions_count")
            final_flavor.append({
                "group": group_val,
                "mentions_count": mentions_count
            })

    # Gestione style
    style_data = doc_originale.get("style", {})
    body_value = style_data.get("body")
    body_desc = style_data.get("body_description")
    food_list = style_data.get("food", [])

    # Ricaviamo la lista di cibi con "name"
    final_food = []
    if isinstance(food_list, list):
        for food_item in food_list:
            fname = food_item.get("name")
            if fname:
                final_food.append({"name": fname})

    # Determiniamo la variety partendo da type_id
    variety_value = mappa_type_id_a_variety(doc_originale.get("type_id", None))

    # Ora costruiamo il documento finale
    final_doc = {
        "_id": new_id,
        "name": final_name,
        "price": final_price,
        "alcohol_percentage": final_alcohol,
        "description": final_description,
        "country": final_country,
        "region": final_region,
        "winery": {
            "id": winery_id,
            "name": winery_name
        },
        "taste": {
            "structure": final_taste_structure,
            "flavor": final_flavor
        },
        "style": {
            "body": body_value,
            "body_description": body_desc,
            "food": final_food
        },
        "provenance": "V",
        "variety": variety_value
    }

    return final_doc

# ------------- Funzione principale -------------
def main():
    all_transformed_wines = []
    
    for directory in DIRECTORIES:
        # Iteriamo sui file .json presenti in ciascuna cartella
        for filename in os.listdir(directory):
            if filename.lower().endswith(".json"):
                file_path = os.path.join(directory, filename)
                # Leggiamo il file
                with open(file_path, "r", encoding="utf-8") as f:
                    data = json.load(f)  # Ci aspettiamo un dizionario con chiave "wines"

                wines_list = data.get("wines", [])
                for wine in wines_list:
                    # Trasforma e aggiunge alla lista
                    transformed = trasforma_vivino(wine)
                    all_transformed_wines.append(transformed)

                # Se abbiamo già raggiunto o superato la soglia MAX_WINES, ci fermiamo
                if len(all_transformed_wines) >= MAX_WINES:
                    break
        # Check se abbiamo raggiunto la soglia, usciamo dal for delle cartelle
        if len(all_transformed_wines) >= MAX_WINES:
            break

    # Se la lista supera i 100000, tagliamo
    if len(all_transformed_wines) > MAX_WINES:
        all_transformed_wines = all_transformed_wines[:MAX_WINES]

    # Salviamo su disco
    with open(OUTPUT_FILE, "w", encoding="utf-8") as out_file:
        json.dump(all_transformed_wines, out_file, indent=4, ensure_ascii=False)

    print(f"Processo completato. Trovati e trasformati {len(all_transformed_wines)} vini.")
    print(f"I dati finali sono stati salvati in '{OUTPUT_FILE}'.")

if __name__ == "__main__":
    main()
