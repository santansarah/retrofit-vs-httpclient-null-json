# Ktor HttpClient vs Retrofit: Missing JSON fields, fields that can be string or numeric, defaults and nulls

If you watch my YouTube channel, then you already know that I like to experiment and play with 
different technologies. In this video, I explore the different JSON responses that come back from 
Ktor's HttpClient vs Retrofit. I'll also go over missing JSON fields, JSON fields that can come
back as a string or a numeric value, and how to handle nulls and defaults in our data classes.

[![HttpClient vs Retrofit](https://img.youtube.com/vi/cAJChWxHvuc/0.jpg)](https://www.youtube.com/watch?v=cAJChWxHvuc)

For this app, I'm using the [Open Food Facts API](https://world.openfoodfacts.org/). Their
[GitHub Repo](https://github.com/openfoodfacts) has a ton of info if you'd like to learn more.

First, let's take a look at the JSON data that I'm working with. Here's a typical "good" response 
that gets the nutrition data for cashews:

> Make note of the field `"carbohydrates_serving": 9`

```json
{
    "code": "0029000016071",
    "product": {
        "brands": "Planters",
        "nutriments": {
            "carbohydrates": 32.14,
            "carbohydrates_100g": 32.14,
            "carbohydrates_serving": 9,
            "carbohydrates_unit": "g",
            "carbohydrates_value": 32.14
        }
    },
    "status": 1,
    "status_verbose": "product found"
}
```

Now, here's the response from a different product. For this one, there _is_ no 
`"carbohydrates_serving": 9` field (at least in Postman and in the browser - more on this later).

```json
{
    "code": "0078742366951",
    "product": {
        "brands": "Great Value",
        "nutriments": {
            "carbohydrates": 33.333333333333,
            "carbohydrates_100g": 33.333333333333,
            "carbohydrates_unit": "g",
            "carbohydrates_value": 33.333333333333
        }
    },
    "status": 1,
    "status_verbose": "product found"
}
```

The response using HttpClient is the same for both products, but in Retrofit, I get this string
response - this is without serializing the body to a data class:

```json
{"code":"0078742366951","product":{"brands":"Great Value",
  "nutriments":{"carbohydrates":33.333333333333,"carbohydrates_100g":33.333333333333,
    "carbohydrates_serving":"","carbohydrates_unit":"g","carbohydrates_value":33.333333333333
    }},"status":1,"status_verbose":"product found"}
```

Here, `"carbohydrates_serving":""` is an empty string! 😵‍💫🤪🤔 
Now we have 3 scenarios:

* Carbs per serving returns a number, like 9, or 26
* or, it's completely missing from the JSON response
* and finally, it could be set to an empty string

So how do we handle this in our apps? The answer for me was different based on my API client. 
I prefer using Ktor's HttpClient in my projects, but for my latest one, I switched back to Retrofit 
just to get some variety in my life. So here's what I ran into while building my latest app, and 
I'll take you along on the journey. 

First, I'll go over a Ktor HttpClient setup, then, I'll go over Retrofit. Here are the corresponding
branches if you want to follow along:

https://github.com/santansarah/retrofit-vs-httpclient-null-json/tree/ktor_httpclient
https://github.com/santansarah/retrofit-vs-httpclient-null-json/tree/retrofit
