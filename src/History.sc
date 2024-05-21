require: dateTime/moment.min.js
    module = sys.zb-common

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
        
        state: HistoryDay
            script:
                var now = moment()
                log(moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'))
                var day = new Date(now).getDate()
                var result = $client.DiaryHistory.filter(d => {var time = new Date(d.Date).getDate();
                    return (day === time)});
                log(result)

        state: HistoryWeek
            script:
                
        state: HistoryMonth
            script:
        
        state: PrepareHistory
            event: telegramCallbackQuery || fromState = "/History/HistoryFull", onlyThisState = true
            if: $request.query =="day"
                go!: /History/HistoryFull/HistoryDay
            
            if: $request.query == "week"
                go!: /History/HistoryFull/HistoryWeek
            
            if: $request.query =="month"
                go!: /History/HistoryFull/HistoryMonth