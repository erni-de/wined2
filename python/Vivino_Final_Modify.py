import json
import uuid

def transform_vivino_document(doc):
    """
    Trasforma un documento Vivino (provenance="V") nello schema semplificato desiderato.
    Ritorna un nuovo dizionario con i campi richiesti, con eventuali campi 'vuoti' a null.
    """
    new_id = str(uuid.uuid4())       # _id
    winery_uuid = str(uuid.uuid4())  # winery.id

    # costruiamo il dizionario finale (con default None per i campi che ci interessano)
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
        "provenance": doc.get("provenance", None),
        "variety": doc.get("variety", None)
    }

    winery_data = doc.get("winery")
    if isinstance(winery_data, dict):
        new_doc["winery"]["name"] = winery_data.get("name", None)

    taste_data = doc.get("taste")
    if isinstance(taste_data, dict):
        structure = taste_data.get("structure", {})
        if isinstance(structure, dict):
            new_doc["taste"]["structure"]["acidity"]   = structure.get("acidity", None)
            new_doc["taste"]["structure"]["fizziness"] = structure.get("fizziness", None)
            new_doc["taste"]["structure"]["intensity"] = structure.get("intensity", None)
            new_doc["taste"]["structure"]["sweetness"] = structure.get("sweetness", None)
            new_doc["taste"]["structure"]["tannin"]    = structure.get("tannin", None)

        flavor_list = taste_data.get("flavor")
        if isinstance(flavor_list, list):
            for flavor_obj in flavor_list:
                group_val = flavor_obj.get("group", None)
                stats_val = flavor_obj.get("stats", None)
                mentions_count = None
                if isinstance(stats_val, dict):
                    mentions_count = stats_val.get("mentions_count", None)

                if group_val is not None:
                    new_doc["taste"]["flavor"].append({
                        "group": group_val,
                        "mentions_count": mentions_count
                    })

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


def filter_and_transform_vivino_docs_keep_winemag(input_file, output_file):
    """
    Legge 'input_file' (JSON con doc di WineMag e Vivino),
    - Se doc.provenance='V': trasforma la struttura
    - Altrimenti lascia invariato (WineMag).
    E salva TUTTI i documenti in 'output_file'.
    """
    with open(input_file, 'r', encoding='utf-8') as f:
        data = json.load(f)  # Supponendo che il file sia una lista di documenti JSON

    new_dataset = []
    for doc in data:
        if doc.get("provenance") == "V":
            # Trasforma
            new_doc = transform_vivino_document(doc)
            new_dataset.append(new_doc)
        else:
            # Non toccarlo
            new_dataset.append(doc)

    # Salviamo su file
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(new_dataset, f, indent=4, ensure_ascii=False)


if __name__ == "__main__":
    filter_and_transform_vivino_docs_keep_winemag("wines_id.json", "wines_final.json")
    print("Trasformazione completata. File generato: wines_final.json")
