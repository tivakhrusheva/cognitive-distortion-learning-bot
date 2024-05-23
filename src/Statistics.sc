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
            var distortion_stats = countValueOccurrencesForAll(filtered_array, "Distortion")
            log("distortion_stats" + toPrettyString(distortion_stats))
            $reactions.answer("distortion_stats" + toPrettyString(distortion_stats));
            var emotion_stats = countValueOccurrencesForAll(filtered_array, "Emotion")
            log("emotion_stats" + toPrettyString(emotion_stats))
            $reactions.answer("emotion_stats" + toPrettyString(emotion_stats));

    
    state: PrepareStats
        event: telegramCallbackQuery || fromState = "/Statistics/StatisticsFull", onlyThisState = true
        script:
            log("bestie im here")
        if: $request.query == "hour"
            go!: /Statistics/StatisticsByPeriod
        
        if: $request.query == "day"
            go!: /Statistics/StatisticsByPeriod
        
        if: $request.query == "week"
            go!: /Statistics/StatisticsByPeriod/
        
        if: $request.query == "month"
            go!: /Statistics/StatisticsFull/StatisticsByPeriod
        
        if: $request.query == "year"
            go!: /Statistics/StatisticsByPeriod
    
    
    # state: PrepareHistory
    #     event: telegramCallbackQuery || fromState = "/History/HistoryFull", onlyThisState = true
    #     if: $request.query == "hour"
    #         go!: /History/HistoryFull/HistoryHour
        
    #     if: $request.query == "day"
    #         go!: /History/HistoryFull/HistoryDay
        
    #     if: $request.query == "week"
    #         go!: /History/HistoryFull/HistoryWeek
        
    #     if: $request.query == "month"
    #         go!: /History/HistoryFull/HistoryMonth
        
    #     if: $request.query == "year"
    #         go!: /History/HistoryFull/HistoryYear
        