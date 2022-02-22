#!/usr/bin/env bb

(require '[babashka.curl :as curl])
(require '[clojure.edn :as edn])
(require '[cheshire.core :as json])
(require 'clojure.pprint)

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime)

(def date (LocalDateTime/now))
(def formatter (DateTimeFormatter/ofPattern "EEEE MMMM dd yyyy"))
(def daily-notion-page-title
  (.format date formatter)) ;;=> "Tuesday October 19 2021"

(def config (edn/read-string (slurp "/Users/briantenggren/projects/notion-templating/config.edn")))
(def journal-database-id (:JOURNAL_ID config))
(def notion-api-key (:NOTION_API_KEY config))
;; you can figure out your url https://www.weather.gov/documentation/services-web-api
(def forecast-url (:FORECAST_URL config))
(def daily-focal-points (:DAILY_FOCUS config))

(def forecast
  (-> (curl/get forecast-url {:throw false})
      :body
      (json/parse-string true)
      :properties
      :periods))

(defn forecast-period->content-block [forecast-period-data]
  (let [period-name (:name forecast-period-data)
        forecast (:detailedForecast forecast-period-data)
        forecast-string (str period-name ": " forecast)]
   {:object :block
    :type :bulleted_list_item
    :bulleted_list_item {:text [{:type :text :text {:content forecast-string}}]}}))

(def weather-content
  (map forecast-period->content-block (take 3 forecast)))

(defn daily-focus->content-block [focus-point]
  {:object :block
   :type :quote
   :quote {:text [{:type :text :text {:content (str focus-point)}}]}})

(def daily-focus-content
  (map daily-focus->content-block daily-focal-points))

(def page-content
  (concat [{:object :block
            :type :heading_2
            :heading_2 {:text [{:type :text :text {:content "Today"}}]}}]
          weather-content
          [{:object :block
            :type :heading_2
            :heading_2 {:text [{:type :text :text {:content "Something to Keep in Mind"}}]}}]
          daily-focus-content
          [{:object :block
            :type :heading_2
            :heading_2 {:text [{:type :text :text {:content "I'm feeling..."}}]}}
           {:object :block
            :type :paragraph
            :paragraph {:text [{:type :text :text {:content " "}}]}}
           {:object :block
            :type :heading_3
            :heading_3 {:text [{:type :text :text {:content "Work Stuff"}}]}}
           {:object :block
            :type :to_do
            :to_do {:checked true
                    :text [{:type :text :text {:content "Generate Daily Notes"}}]}}
           {:object :block
            :type :heading_3
            :heading_3 {:text [{:type :text :text {:content "Personal"}}]}}
           {:object :block
            :type :to_do
            :to_do {:checked false
                    :text [{:type :text :text {:content " "}}]}}
           {:object :block
            :type :heading_3
            :heading_3 {:text [{:type :text :text {:content "For Tomorrow"}}]}}
           {:object :block
            :type :to_do
            :to_do {:checked false
                    :text [{:type :text :text {:content " "}}]}}]))

(def request-url "https://api.notion.com/v1/pages")
(def request-headers
  {"Authorization" notion-api-key
   "Content-Type" "application/json"
   "Notion-Version" "2021-08-16"})
(def request-body
  {:parent {:database_id journal-database-id}
   :properties {"Name" {:title [{:text {:content daily-notion-page-title}}]}
                "Stuff" {:multi_select [{:name "Daily"} {:name "Work"}]}}
   :children page-content})

(println "Beep Boop")
(println "Creating a new Notion Journal entry:" daily-notion-page-title)
(def response (curl/post request-url
                         {:body (json/generate-string request-body)
                          :headers request-headers
                          :throw false}))
;(println (str "Notion says: " (:status response) (:body response)))
(println (str "Notion says: " (:status response)))



