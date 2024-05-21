theme: /Settings
    
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
        event: telegramCallbackQuery || fromState = "/Settings/DiaryReminderSetting", onlyThisState= false
        if: $request.query ==  "diary_reminder_frequency"
            go!: /Settings/DiaryReminderSetting/DiaryReminderFrequency
        elseif: $request.query == "theory_reminder_frequency"
            go!: /Settings/DiaryReminderSetting/TheoryReminderFrequency
    
    state: StatisticsFrequencyChoice
        event: telegramCallbackQuery || fromState = "/Settings/SettingsDiaryStatisticsSetting/StatisticsFrequency", onlyThisState = true
        if: $request.query == "diary_reminder_settings"
            go!: /Settings/DiaryReminderSetting
        elseif: $request.query == "diary_stats_settings"
            go!: /Settings/DiaryStatisticsSetting
    
    state: DiaryReminderSetting
        a: Выберите настройку
        inlineButtons:
            { text: "Частота напоминаний о ведении дневника", callback_data: "diary_reminder_frequency" }
            { text: "Частота напоминаний об изучении теории", callback_data: "theory_reminder_frequency" }
        
        state: DiaryReminderFrequency
            a: Как часто вы хотели бы получать напоминания о заполнении дневника автоматических мыслей?
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }
        
        state: TheoryReminderFrequency
            a: Как часто вы хотели бы получать напоминания об изучении теории?
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }
        
    state: DiaryStatisticsSetting
        a: Выберите настройку
        
        state: StatisticsFrequency
            a: Как часто вы хотели бы получать сообщения с вашей статистикой?
            inlineButtons:
                { text: "Раз в день", callback_data: "once_a_day" }
                { text: "Два раза в неделю", callback_data: "twice_a_week" }
                { text: "Раз в неделю", callback_data: "once_a_week" }
                { text: "Раз в две недели", callback_data: "once_two_weeks" }
                { text: "Раз в месяц", callback_data: "once_a_month" }
                { text: "Свой вариант", callback_data: "another_option" }