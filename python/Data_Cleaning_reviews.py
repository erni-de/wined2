import os
import json

def filter_wine_data(input_directory, output_directory):
    # Ensure the output directory exists
    os.makedirs(output_directory, exist_ok=True)

    for file_name in os.listdir(input_directory):
        if file_name.endswith(".json"):
            input_path = os.path.join(input_directory, file_name)
            output_path = os.path.join(output_directory, file_name)

            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            # Preparing the wines array that will store cleaned data
            filtered_data = {
                "wines": []
            }

            # Taking only the attributes we want to treat. In this case, only id and name for wines
            for wine in data.get("wines", []):

                # Ensure wine has the expected fields (id and name)
                filtered_wine = {
                    # General data from the wine (only id and name)
                    "id": wine.get("wine_id"),  # Wine id
                    "name": wine.get("wine_name"),  # Wine name

                    # Datas about the reviews (same as before)
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

# Path for the folders
input_directory = "../data/reviews/vivino/"  # Replace with your input directory
output_directory = "../data/Data_Cleaning/vivino_reviews/"  # Replace with your output directory

# Calling the function three times for different regions (French, Italian, US wines)
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
