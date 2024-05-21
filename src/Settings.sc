theme: /Settings
    
    
    state: Pushgate
        script:
            if ($context.session.lastState == "/Settings/LearningReminderSetting/DiaryReminderFrequency") {
                var pushback = $pushgate.createEvent(
                    $request.channelType,
                    $request.botId,
                    $request.channelUserId,
                    "DiaryNotification",
                    {}
                );
            };
            if ($context.session.lastState == "/Settings/LearningReminderSetting/TheoryReminderFrequency") {
                var pushback = $pushgate.createEvent(
                    $request.channelType,
                    $request.botId,
                    $request.channelUserId,
                    "TheoryNotification",
                    {}
                );
            };
            if ($context.session.lastState == "/Settings/DiaryStatisticsSetting/StatisticsFrequency") {
                var pushback = $pushgate.createEvent(
                    $request.channelType,
                    $request.botId,
                    $request.channelUserId,
                    "StatisticsNotification",
                    {}
                );
            };
            
    
    state: SettingChoice
        q!: $regex</settings>
        a: {{contents.setting_hint}}
        inlineButtons:
            { text: "Напоминания о работе с когнитивными искажениями", callback_data: "diary_reminder_settings" }
            { text: "Отправка статистики по дневнику", callback_data: "diary_stats_settings" }
    
    state: GeneralSettingChoice
        event: telegramCallbackQuery || fromState = "/Settings/SettingChoice", onlyThisState = true
        if: $request.query == "diary_reminder_settings"
            go!: /Settings/DiaryReminderSetting
        elseif: $request.query == "diary_stats_settings"
            go!: /Settings/DiaryStatisticsSetting
    
    state: ReminderFrequencyChoice
        event: telegramCallbackQuery || fromState = "/Settings/LearningReminderSetting", onlyThisState= false
        if: $request.query ==  "diary_reminder_frequency"
            go!: /Settings/LearningReminderSetting/DiaryReminderFrequency
        elseif: $request.query == "theory_reminder_frequency"
            go!: /Settings/LearningReminderSetting/TheoryReminderFrequency
    
    state: StatisticsFrequencyChoice
        event: telegramCallbackQuery || fromState = "/Settings/SettingsDiaryStatisticsSetting/StatisticsFrequency", onlyThisState = true
        if: $request.query == "diary_reminder_settings"
            go!: /Settings/DiaryReminderSetting
        elseif: $request.query == "diary_stats_settings"
            go!: /Settings/DiaryStatisticsSetting
    
    
    state: LearningReminderSetting
        a: Выберите настройку
        inlineButtons:
            { text: "Частота напоминаний о ведении дневника", callback_data: "diary_reminder_frequency" }
            { text: "Частота напоминаний об изучении теории", callback_data: "theory_reminder_frequency" }
        
        state: LearningReminderSetting
            a: Выберите настройку
            inlineButtons:
                { text: "Отключить", callback_data: "theory_reminder_turn_off" }
                { text: "Изменить настройки", callback_data: "theory_reminder_change" }
        
        state: LearningReminderTurnOff
            a: {{contents.learning_reminder_deleted}}
            script:
                $pushgate.cancelEvent($cient.ReminderTheoryId);
                delete $cient.ReminderTheoryId
        
        state: DiaryReminderTurnOff
            a: {{contents.diary_reminder_deleted}}
            script:
                $pushgate.cancelEvent($cient.ReminderDiaryId);
                delete $cient.ReminderDiaryId;
        
        state: DiaryReminderFrequency
            a: {{contents.diary_frequency}}
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }
        
        state: TheoryReminderFrequency
            a: {{contents.theory_frequency}}
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }
        
    state: DiaryStatisticsSetting
        a: Выберите настройку
        
        state: StaticticsNotifTurnOff
            a: {{contents.statistics_notification_deletetd}}
            script:
                $pushgate.cancelEvent($cient.StatisticsNotificationId);
                delete $cient.StatisticsNotificationId;
        
        state: StatisticsFrequency
            a: {{contents.stats_frequency}}
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }
            
            state: Day
                a: {{contents.stats_frequency}}
                script: 
                    $client.StatisticsNMonth = $request.query
                inlineButtons:
                    { text: "Понедельник", callback_data: "monday" }
                    { text: "Вторник", callback_data: "tuesday" }
                    { text: "Среда", callback_data: "wednesday" }
                    { text: "Четверг", callback_data: "thursday" }
                    { text: "Пятница", callback_data: "friday" }
                    { text: "Суббота", callback_data: "saturday" }
                    { text: "Воскресенье", callback_data: "sunday" }
            
                state: ExactTime
                    event!: telegramCallbackQuery || fromState = "/Settings/DiaryStatisticsSetting/StatisticsFrequency/Day", onlyThisState = true
                    a: {{contents.time}}
                    script: 
                        $client.StatisticsDay = $request.query
                    inlineButtons:
                        { text: "00:00", callback_data: "0" }
                        { text: "6:00", callback_data: "6" }
                        { text: "8:00", callback_data: "8" }
                        { text: "10:00", callback_data: "10" }
                        { text: "12:00", callback_data: "12" }
                        { text: "14:00", callback_data: "14" }
                        { text: "16:00", callback_data: "16" }
                        { text: "18:00", callback_data: "18" }
                        { text: "20:00", callback_data: "20" }
                        { text: "22:00", callback_data: "22" }
                        { text: "Свой вариант", callback_data: "another_option" }
                
                state: Pushgate
                    event: telegramCallbackQuery || fromState = "/Settings/SettingsDiaryStatisticsSetting/StatisticsFrequency", onlyThisState = true
                    script:
                        # $client.time = $request.query
                        # if ($context.session.lastState == "/Settings/LearningReminderSetting/DiaryReminderFrequency") {
                        #     var pushback = $pushgate.createEvent(
                        #         $request.channelType,
                        #         $request.botId,
                        #         $request.channelUserId,
                        #         "DiaryNotification",
                        #         {{"text": "Скидка только для вас!"}}
                        #     );
                        # };
    
    state: time
        q!: время
        script:
            function convertTZ(date, tzString) {
                return new Date((typeof date === "string" ? new Date(date) : date).toLocaleString("ru-RU", {timeZone: tzString}));   
            }
            var now = moment()
            log("now" + now)
            log("now formated" + moment(now).utcOffset(180).format('YYYY-MM-DD HH:mm'))

    
