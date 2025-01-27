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

            #Qui prendo solo gli attributi che ci interessano
            for wine in data.get("wines", []):
                
                filtered_wine = {

                    "id": wine.get("wine_id"),  
                    "name": wine.get("wine_name"),  

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

            #Salvo
            with open(output_path, "w", encoding="utf-8") as output_file:
                json.dump(filtered_data, output_file, indent=4, ensure_ascii=False)


input_directory = "../data/reviews/vivino/"  
output_directory = "../data/Data_Cleaning/vivino_reviews/" 

#Chiamo la funzione 3 volte poiché abbiamo tre regioni diverse di dati
print("Cleaning France reviews...")
input_directory = input_directory + "R_EN_FR"
output_directory = output_directory + "R_EN_FR"
filter_wine_data(input_directory, output_directory)

print("Cleaning Italian reviews...")
input_directory = "../data/reviews/vivino/" + "R_EN_IT"
output_directory = "../data/Data_Cleaning/vivino_reviews/" + "R_EN_IT"
filter_wine_data(input_directory, output_directory)

print("Cleaning US reviews...")
input_directory = "../data/reviews/vivino/" + "R_EN_US"
output_directory = "../data/Data_Cleaning/vivino_reviews/" + "R_EN_US"
filter_wine_data(input_directory, output_directory)
