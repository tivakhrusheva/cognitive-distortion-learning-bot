require: dateTime/moment.min.js
    module = sys.zb-common

require: ./data/history_content.yaml
    var = history_contents

theme: /History
    
    
    state: HistoryFull
        q!: $regex</history>
        a: {{history_contents.choose_period}}
        inlineButtons:
            { text: "Час", callback_data: "hour" }
            { text: "День", callback_data: "day" }
            { text: "Неделя", callback_data: "week" }
            { text: "Месяц", callback_data: "month" }
        
        state: HistoryHour
            script:
                var now = moment()
                log(moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'))
                var hour = new Date(now).getHours()
                var result = $client.DiaryHistory.filter(function(d) {
                    var time = new Date(d.Date).getHours();
                    return (hour === time);
                });
                log(result)
        
        state: HistoryDay
            script:
                var now = moment()
                log(moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'))
                var day = new Date(now).getDate()
                var result = $client.DiaryHistory.filter(function(d) {
                    var time = new Date(d.Date).getDate();
                    return (day === time);
                });
                log("result" + toPrettyString(result))

        state: HistoryWeek
            q!: тест время
            script:
                var today = new Date();
                var lastWeekEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1);
                var lastWeekStart = new Date(today.getFullYear(), today.getMonth(), today.getDate()-6);
                log("Seven Days Before was " + lastWeekStart.toLocaleString());
                log("Now it is " + lastWeekEnd.toLocaleString());
                var resultWeek = $client.DiaryHistory.filter(function(d) {
                    var time = new Date(d.Date).getTime();
                    log("time" + time);
                    log("lastWeekStart.getTime()" + lastWeekStart.getTime());
                    log("lastWeekEnd.getTime()" + lastWeekEnd.getTime());
                    return (time >= lastWeekStart.getTime() && time < lastWeekEnd.getTime());
                });
                log("resultWeek" + toPrettyString(resultWeek))
                
        state: HistoryMonth
            script:
                var today = new Date();
                var lastMonthEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1);
                var lastMonthStart = new Date(today.getFullYear(), today.getMonth(), today.getDate()-30);
                log("30 Days Before was " + lastMonthStart.toLocaleString());
                log("Now it is " + lastMonthEnd.toLocaleString());
                var resultMonth = $client.DiaryHistory.filter(function(d) {
                    var time = new Date(d.Date).getTime();
                    log("time" + time);
                    log("lastMonthStart.getTime()" + lastMonthStart.getTime());
                    log("lastMonthEnd.getTime()" + lastMonthEnd.getTime());
                    return (time >= lastMonthStart.getTime() && time < lastMonthEnd.getTime());
                });
                log("lastMonthEnd" + toPrettyString(lastMonthEnd))
                
                
        state: HistoryYear
            script:
                var lastYearStart = new Date(today.getFullYear()-1, 0, 1);
                var lastYearEnd = new Date(today.getFullYear(), 0, 1);

        
        state: PrepareHistory
            event: telegramCallbackQuery || fromState = "/History/HistoryFull", onlyThisState = true
            if: $request.query == "hour"
                go!: /History/HistoryFull/HistoryHour
            
            if: $request.query == "day"
                go!: /History/HistoryFull/HistoryDay
            
            if: $request.query == "week"
                go!: /History/HistoryFull/HistoryWeek
            
            if: $request.query == "month"
                go!: /History/HistoryFull/HistoryMonth