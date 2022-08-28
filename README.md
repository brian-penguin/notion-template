# Notion Templating

Every day I work, I create a new notion doc with the title being todays current date like
```
Tuesday October 19 2021
```
I then fill it with a quick "how I'm feeling" section, a personal todo list, and a work todolist. Sometimes I will add other random thoughts and or ideas around stuff that's not quite formulated enough to deserve it's own page. Sometimes I will reference linked other pages and want those to be there.

### What this is
This is a script that's going to do some of that daily templating for me. It should be able to:
- [x] Create a new page with the date as the title
- [x] Populate the weather (this would be cool to do dynamically based on my location but first pass is my default work location)
- [x] Start the two todo lists I use most
- [ ] A dumb quote or tweet or something stupid in my timeline
- [ ] ???
- [ ] Profit

### Here, have a picture
<img width="977" alt="image" src="https://user-images.githubusercontent.com/7143499/138022609-c9ef6960-594e-45ce-b87f-0bd6d8167168.png">


### Dependencies
- This script uses babashka as a native scripting tool. It uses the shebang syntax to point to a usr/local/bb symlink. You can find out how to install that here: https://github.com/babashka/babashka (I brew installed it)

### Setting up
- You will need to create a new integration
- You will need a notion api key
- You will need to figure out your database id
- You will add both of those to a config.edn file at the root of your project (see config-example.edn)
- You will need to add that integration to your database / page
- You can figure out your weather service url here https://www.weather.gov/documentation/services-web-api


### Running 

``` sh
$ ./create_daily_notion.clj
```

### Useful links
- https://developers.notion.com/reference/intro
- https://www.weather.gov/documentation/services-web-api
- https://github.com/babashka/babashka
