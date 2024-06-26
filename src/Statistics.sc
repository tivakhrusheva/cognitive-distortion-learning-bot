theme: /Statistics
    
    state: StatisticsFull
        q!: $regex</statistics>
        a: {{history_contents.choose_period}}
        inlineButtons:
            # { text: "Час", callback_data: "hour" }
            { text: "День", callback_data: "day" }
            { text: "Неделя", callback_data: "week" }
            { text: "Месяц", callback_data: "month" }
            { text: "Год", callback_data: "year" }
        
    state: StatisticsByPeriod
        script:
            var filtered_array = filterByPeriod($client, $request.query)
            var distortion_stats = countValueOccurrencesForAll(filtered_array, "Distortion")
            var to_show1 =  prepareStatistics(distortion_stats, "distortion")
            log("distortion_stats" + to_show)
            if ($request.query == "hour") { 
                var to_say = "✅Статистика ваших запросов за час готова:"
            }
            if ($request.query == "day") { 
                var to_say = "✅Статистика ваших запросов за день готова:"
            }
            if ($request.query == "week") { 
                var to_say = "✅Статистика ваших запросов за неделю готова:"
            }
            if ($request.query == "month") { 
                var to_say = "✅Статистика ваших запросов за месяц готова:"
            }
            if ($request.query == "year") { 
                var to_say = "✅Статистика ваших запросов за год готова:"
            }
            
            $reactions.answer(to_say)
            var emotion_stats = countValueOccurrencesForAll(filtered_array, "Emotion")
            var to_show2 = prepareStatistics(emotion_stats, "emotion")
            var to_show = to_show1 + to_show2
            log("emotion_stats\n\n" + to_show2)
            $reactions.answer(to_show);
            $reactions.inlineButtons({ text: "Ещё запрос", callback_data: "one_more_request" });
            $reactions.inlineButtons({ text: "В меню", callback_data: "to_menu" });

    
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
    
    
    state: Navigate
        event: telegramCallbackQuery || fromState = "/Statistics/StatisticsByPeriod", onlyThisState = true
        script:
            log("bestie im here")
        if: $request.query == "one_more_request"
            go!: /Statistics/StatisticsFull
        
        elseif: $request.query == "to_menu"
            go!: /Start
    
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
        