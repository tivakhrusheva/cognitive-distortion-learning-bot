require: ./data/history_content.yaml
    var = history_contents

theme: /History
    
    state: HistoryFull
        q!: $regex</history>
        a: {{history_contents.choose_period}}
        inlineButtons:
            { text: "День", callback_data: "day" }
            { text: "Неделя", callback_data: "week" }
            { text: "Месяц", callback_data: "month" }
        
        state: PrepareHistory
            event: telegramCallbackQuery || fromState = "/History/HistoryFull", onlyThisState = true
            script:
                log($client.DiaryHistory)