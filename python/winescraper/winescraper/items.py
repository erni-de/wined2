# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html


from scrapy.item import Item, Field


class ReviewItem(Item):
    wine_reviewed = Field()
    stars = Field()
    review_text = Field()
    reviewer_name = Field()
    date = Field()
