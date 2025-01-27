import os
import json

def filter_wine_data(input_directory, output_directory):
    os.makedirs(output_directory, exist_ok=True)

    for file_name in os.listdir(input_directory):
        if file_name.endswith(".json"):
            input_path = os.path.join(input_directory, file_name)
            output_path = os.path.join(output_directory, file_name)

            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            filtered_data = {
                "wines": []
            }

            #Salvo quello che m'interessa
            for wine in data.get("wines", []):
                filtered_wine = {
                    
                    "id": wine.get("id"),
                    "name": wine.get("name"),
                    "type_id": wine.get("type_id"),
                    "price": wine.get("price"),
                    "alcohol_percentage": wine.get("alcohol_percentage"),
                    "description": wine.get("description"),
                    "country": wine.get("country"),

                    "region": {
                        "name": wine.get("region", {}).get("name")
                    },
                    
                    #Cantina
                    "winery": {
                        "id": wine.get("winery", {}).get("id"),
                        "name": wine.get("winery", {}).get("name"),
                        "status": wine.get("winery", {}).get("status")
                    },

                    #Campo taste e flavor salvo le info per Vivino                    
                   "taste": {
                        "structure": {
                        k: (wine.get("taste", {}).get("structure", {}) or {}).get(k, None)
                        for k in ["acidity", "fizziness", "intensity", "sweetness", "tannin", "user_structure_count"]
                        },
    
                        "flavor": [
                        {k: (flavor_item.get(k, None) if isinstance(flavor_item, dict) else None) for k in ["group", "stats", "count"]} 
                        for flavor_item in (wine.get("taste", {}).get("flavor", []) or [])
                        ]
                    },
                    
                    #Campo Food
                    "style": {k: (wine.get("style", {}) or {}).get(k, None) for k in ["body", "body_description", "food"]},
                    
                    #Reviews
                    "reviews": [
                        {
                            "id": review.get("id"),
                            "rating": review.get("rating"),
                            "text": review.get("note"),
                            "language": review.get("language"),
                            "created_at": review.get("created_at"),
                            "user": {
                                "id": review.get("user", {}).get("id"),
                                "statistics": review.get("user", {}).get("statistics")
                            }
                        }

                        for review in wine.get("reviews", [])
                    ]
                }

                filtered_data["wines"].append(filtered_wine)

            with open(output_path, "w", encoding="utf-8") as output_file:
                json.dump(filtered_data, output_file, indent=4, ensure_ascii=False)

input_directory = "../data/wines/vivino/scraped/"  
output_directory = "../data/Data_Cleaning/"  

#Chiamo la funzione 3 volte poiché abbiamo tre regioni diverse di dati
print("Cleaning France wines...")
input_directory = input_directory + "EN_FR"
output_directory = output_directory + "EN_FR"
filter_wine_data(input_directory, output_directory)

input_directory = "../data/wines/vivino/scraped/"  
output_directory = "../data/Data_Cleaning/" 

print("Cleaning Italian wines...")
input_directory = input_directory + "EN_IT"
output_directory = output_directory + "EN_IT"
filter_wine_data(input_directory, output_directory)

input_directory = "../data/wines/vivino/scraped/"  
output_directory = "../data/Data_Cleaning/"  

print("Cleaning Us wines...")
input_directory = input_directory + "EN_US"
output_directory = output_directory + "EN_US"
filter_wine_data(input_directory, output_directory)

    
