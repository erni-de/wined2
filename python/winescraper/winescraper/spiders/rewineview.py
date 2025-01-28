import scrapy
import re
from datetime import datetime
from urllib.parse import urljoin

class RewineviewSpider(scrapy.Spider):
    #system variables
    name = "rewineview"
    allowed_domains = ["www.wineshop.it"] 
    start_urls = ["https://www.wineshop.it/en/"]

    wine_categories = ["red-wines","white-wines","rose-wines","sparkling-wines","dessert-wines"]

    #patterns to form jsons
    body_pattern = r"</table>\s*(.*?)\s*<small class=\"date\">"
    date_pattern = r"\(Posted on (\d{1,2}/\d{1,2}/\d{4})\)"
    rating_pattern = r'width:(\d+)%'


    def parse(self, response):
        for category in self.wine_categories:
            full_url = urljoin(response.url, category + "?p=10000")
            yield scrapy.Request(full_url, callback=self.parse_category)


    def parse_category(self, response):
        res = response.css('li.current::text').get()
        if res is None :
            a = 1
        else :
            a = int(res)
        
        for i in range(a + 1):
            full_url = re.sub(r'\d{5}$', '', response.url)+ str(i)
            yield scrapy.Request(full_url, callback=self.parse_pages)


    def parse_pages(self, response):
        # Get all relative links
        product_links = response.css('h2.product-name > a::attr(href)').getall()
        for link in product_links:

            full_url = urljoin(response.url, link)  # Convert to full URL
            yield scrapy.Request(full_url, callback=self.parse_product)


    def parse_product(self, response): # parse reviews in wine
            wine = response.css('div.product-name h1::text').get()
            titles = response.css('dt')
            bodies = response.css('dd')
            print(f"Parsing {wine} from : {response.url} ")
            for i in range(len(titles)) :
                yield {
                    'wine' : wine,
                    'reviewer' : None,
                    'stars' : int(re.search(self.rating_pattern, bodies[i].css('div.rating').attrib['style']).group(1)) / 20,
                    'title' : titles[i].css('span.heading::text').get(),
                    'body' : re.search(self.body_pattern , bodies[i].get()).group(1),
                    'date' : datetime.strptime(re.search(self.date_pattern, bodies[i].css('small.date::text').get()).group(1), "%m/%d/%Y").date()
                }

    