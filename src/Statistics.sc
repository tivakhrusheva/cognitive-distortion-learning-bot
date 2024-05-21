theme: /Statistics
    
    state: StatisticsFull
        q!: $regex</statistics>
        a: {{history_contents.choose_period}}
        inlineButtons:
            { text: "Час", callback_data: "hour" }
            { text: "День", callback_data: "day" }
            { text: "Неделя", callback_data: "week" }
            { text: "Месяц", callback_data: "month" }
            { text: "Год", callback_data: "year" }
        
        state: StatisticsByPeriod
            script:
                var filtered_array = filterByPeriod($client, $request.query)
                log(countValueOccurrencesForAll(filtered_array, "Distortion"))
                log(countValueOccurrencesForAll(filtered_array, "Emotion"))

    
    state: PrepareHistory
            event: telegramCallbackQuery || fromState = "/Statistics/Statistics", onlyThisState = true
            if: $request.query == "hour"
                go!: /StatisticsFull/StatisticsByPeriod
            
            if: $request.query == "day"
                go!: /History/StatisticsByPeriod
            
            if: $request.query == "week"
                go!: /History/StatisticsByPeriod/
            
            if: $request.query == "month"
                go!: /History/StatisticsByPeriod
            
            if: $request.query == "year"
                go!: /History/StatisticsByPeriod