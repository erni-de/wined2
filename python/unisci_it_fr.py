import os
import json
import uuid

def transform_vivino_document(doc):
    """
    Trasforma un documento Vivino (provenance='V') nello schema semplificato desiderato.
    Ritorna un nuovo dizionario con i campi richiesti, con eventuali campi 'vuoti' a null.
    """
    new_id = str(uuid.uuid4())       # _id
    winery_uuid = str(uuid.uuid4())  # winery.id

    new_doc = {
        "_id": new_id,
        "name": doc.get("name", None),
        "price": doc.get("price", None),
        "alcohol_percentage": doc.get("alcohol_percentage", None),
        "description": doc.get("description", None),
        "country": doc.get("country", None),
        "region": doc.get("region", None),
        "winery": {
            "id": winery_uuid,
            "name": None
        },
        "taste": {
            "structure": {
                "acidity": None,
                "fizziness": None,
                "intensity": None,
                "sweetness": None,
                "tannin": None
            },
            "flavor": []
        },
        "style": {
            "body": None,
            "body_description": None,
            "food": []
        },
        "provenance": doc.get("provenance", "V"),  # Impostiamo di default 'V'
        "variety": doc.get("variety", None)
    }

    # Estrazione dati "winery"
    winery_data = doc.get("winery")
    if isinstance(winery_data, dict):
        new_doc["winery"]["name"] = winery_data.get("name", None)

    # Estrazione dati "taste"
    taste_data = doc.get("taste")
    if isinstance(taste_data, dict):
        structure = taste_data.get("structure", {})
        if isinstance(structure, dict):
            new_doc["taste"]["structure"]["acidity"] = structure.get("acidity", None)
            new_doc["taste"]["structure"]["fizziness"] = structure.get("fizziness", None)
            new_doc["taste"]["structure"]["intensity"] = structure.get("intensity", None)
            new_doc["taste"]["structure"]["sweetness"] = structure.get("sweetness", None)
            new_doc["taste"]["structure"]["tannin"] = structure.get("tannin", None)

        flavor_list = taste_data.get("flavor")
        if isinstance(flavor_list, list):
            for flavor_obj in flavor_list:
                group_val = flavor_obj.get("group", None)
                # Nella struttura originale di Vivino a volte "stats" contiene "mentions_count"
                # Nel tuo codice era doc["stats"]["mentions_count"] se presente
                stats_val = flavor_obj.get("stats", {})
                mentions_count = None
                if isinstance(stats_val, dict):
                    mentions_count = stats_val.get("mentions_count", None)

                if group_val is not None:
                    new_doc["taste"]["flavor"].append({
                        "group": group_val,
                        "mentions_count": mentions_count
                    })

    # Estrazione dati "style"
    style_data = doc.get("style")
    if isinstance(style_data, dict):
        new_doc["style"]["body"] = style_data.get("body", None)
        new_doc["style"]["body_description"] = style_data.get("body_description", None)

        food_list = style_data.get("food")
        if isinstance(food_list, list):
            for food_obj in food_list:
                name_val = food_obj.get("name", None)
                if name_val is not None:
                    new_doc["style"]["food"].append({"name": name_val})

    return new_doc


def unisci_file_it_fr(cartelle_input, output_file):
    """
    - Legge tutti i file .json presenti nelle cartelle specificate in 'cartelle_input'.
    - Da ciascun file estrae i documenti all'interno di "wines".
    - Imposta provenance = 'V' su ciascun documento, li trasforma con 'transform_vivino_document',
      e li accumula in una lista.
    - Salva la lista risultante nel file 'output_file' come array JSON.
    """
    tutti_i_documenti = []

    for cartella in cartelle_input:
        for nome_file in os.listdir(cartella):
            if nome_file.endswith(".json"):
                path_file = os.path.join(cartella, nome_file)
                with open(path_file, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    # I file hanno tipicamente la forma { "wines": [ {...}, {...} ] }
                    wines_list = data.get("wines", [])
                    if isinstance(wines_list, list):
                        for wine_doc in wines_list:
                            # Impostiamo (o sovrascriviamo) la provenance a 'V'
                            wine_doc["provenance"] = "V"
                            # Trasformiamo la struttura
                            nuovo_doc = transform_vivino_document(wine_doc)
                            tutti_i_documenti.append(nuovo_doc)

    # Salviamo tutti i documenti in un unico file JSON
    with open(output_file, 'w', encoding='utf-8') as f_out:
        json.dump(tutti_i_documenti, f_out, indent=4, ensure_ascii=False)


if __name__ == "__main__":
    # Cartelle di input
    cartelle = ["./it", "./fr"]
    # File di output unificato
    file_output = "wines_merged.json"

    unisci_file_it_fr(cartelle, file_output)
    print(f"Unione e trasformazione completate. File generato: {file_output}")
