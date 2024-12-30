import os
import json

def filter_wine_data(input_directory, output_directory):
    #Ensure the output directory exists
    os.makedirs(output_directory, exist_ok=True)

    for file_name in os.listdir(input_directory):
        if file_name.endswith(".json"):
            input_path = os.path.join(input_directory, file_name)
            output_path = os.path.join(output_directory, file_name)

            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            #Preparing the wines array that will store cleaned data
            filtered_data = {
                "wines": []
            }

            #Taking only the attributes we want to treat. In this case 
            for wine in data.get("wines", []):
                filtered_wine = {
                    
                    #General datas from the wine
                    "id": wine.get("id"),
                    "name": wine.get("name"),
                    "type_id": wine.get("type_id"),
                    "price": wine.get("price"),

                    #Region of the production of the wine
                    "region": {
                        "name": wine.get("region", {}).get("name")
                    },
                    
                    #Datas about the winery
                    "winery": {
                        "id": wine.get("winery", {}).get("id"),
                        "name": wine.get("winery", {}).get("name"),
                        "status": wine.get("winery", {}).get("status")
                    },
                    
                    #Datas about the taste 
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

                    #Datas for food premium users
                    "style": {k: (wine.get("style", {}) or {}).get(k, None) for k in ["body", "body_description", "food"]},
                    
                    #Datas about the reviews
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

            # Write the filtered data to the output file
            with open(output_path, "w", encoding="utf-8") as output_file:
                json.dump(filtered_data, output_file, indent=4, ensure_ascii=False)

#Path for the folders
input_directory = "../data/wines/vivino/scraped/"  # Replace with your input directory
output_directory = "../data/Data_Cleaning/"  # Replace with your output directory

#Calling the function three times
print("Cleaning France wines...")
input_directory = input_directory + "EN_FR"
output_directory = output_directory + "EN_FR"
filter_wine_data(input_directory, output_directory)

input_directory = "../data/wines/vivino/scraped/"  # Replace with your input directory
output_directory = "../data/Data_Cleaning/"  # Replace with your output directory

print("Cleaning Italian wines...")
input_directory = input_directory + "EN_IT"
output_directory = output_directory + "EN_IT"
filter_wine_data(input_directory, output_directory)

input_directory = "../data/wines/vivino/scraped/"  # Replace with your input directory
output_directory = "../data/Data_Cleaning/"  # Replace with your output directory

print("Cleaning Us wines...")
input_directory = input_directory + "EN_US"
output_directory = output_directory + "EN_US"
filter_wine_data(input_directory, output_directory)

    
