import re
import json
import requests
import time
import sys
import random

headers = {
    "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0",
}

with open('/home/erni/scraping/v_folder/winescraper/winescraper/data_json/wine_links.json', 'r') as file:
    data = json.load(file)

    with open('/home/erni/scraping/v_folder/winescraper/winescraper/data_json/vivino_reviews.json', 'w') as file_2:
        print("[", file = file_2)

        counter = 1

        for idx, d in enumerate(data) :
            url = d['wine_link']
            n_reviews = random.randint(1,20)
            print(f"Wine n° : {counter}, N° of Reviews : {n_reviews}")
            counter = counter + 1
            try : 
                id = re.search(r"/(\d{5,})", url).group(1)
                year = re.search(r"year=(\d+)", url).group(1)

                api_url = "https://www.vivino.com/api/wines/" + id + "/reviews?per_page=" + str(n_reviews) + "&year=" + year

                data__ = requests.get(api_url.format(id=id), headers=headers).json()
                
                for i in range(len(data__["reviews"])):
                    
                    review = {
                    "review_id": int(data__['reviews'][i]['id']),
                    "rating" : float(data__['reviews'][i]['rating']),
                    "wine" : str(data__['reviews'][i]['vintage']['wine']['name']),
                    "review" : str(data__['reviews'][i]['note']),
                    "time" : str(data__['reviews'][i]['created_at']),
                    "user" : None
                    }

                    print(json.dumps(review) + ",", file = file_2)
                    
                time.sleep(1)    
            except KeyboardInterrupt:
                print("Quitted")
                print("]", file = file_2)
                sys.exit()
            except Exception as e: 
                print(f"An error occured : {e}")     
        print("[", file = file_2)




"""
id = 3875071

data = requests.get(api_url.format(id=id), headers=headers).json()

# uncomment this to print all data:
# print(json.dumps(data, indent=4))

for r in data["reviews"]:
    print(r["note"])
    print("-" * 80)"""