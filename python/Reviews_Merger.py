import os
import json

#Modifico le recensioni che abbiamo, così come sono strutturate è impossibile
#Le uniamo vediamo come viene
def process_initial_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        reviews = json.load(file)

    for review in reviews:
        review["name"] = review.pop("wine", None)
        review["rating"] = review.pop("stars", None)
        review["text"] = review.pop("body", None)
        review["created_at"] = review.pop("date", None)
        
        if "title" in review:
            del review["title"]

    return reviews

def process_reviews_from_folders(folder_paths):
    combined_reviews = []

    for folder_path in folder_paths:

        for file_name in os.listdir(folder_path):

            file_path = os.path.join(folder_path, file_name)

            if file_path.endswith(".json"):
                with open(file_path, 'r', encoding='utf-8') as file:
                    reviews = json.load(file)
                    combined_reviews.extend(reviews)

    return combined_reviews

def main():
    initial_file_path = "../data/reviews/wineshop/wineshop_reviews.json"

    review_folders = [
        "../data/Data_Cleaning/vivino_reviews/R_EN_FR",
        "../data/Data_Cleaning/vivino_reviews/R_EN_IT",
        "../data/Data_Cleaning/vivino_reviews/R_EN_US"
    ]

    print("Elaborazione del file iniziale...")
    initial_reviews = process_initial_file(initial_file_path)

    print("Elaborazione delle recensioni nelle cartelle...")
    additional_reviews = process_reviews_from_folders(review_folders)

    all_reviews = initial_reviews + additional_reviews

    output_file_path = "../data/reviews/combined_reviews.json"
    with open(output_file_path, 'w', encoding='utf-8') as output_file:
        json.dump(all_reviews, output_file, indent=4, ensure_ascii=False)

    print(f"Recensioni combinate salvate in: {output_file_path}")

if __name__ == "__main__":
    main()
