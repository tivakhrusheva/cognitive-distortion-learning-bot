theme: /Settings
    
    state: SettingChoice
        q!: $regex</settings>
        a: {{contents.setting_hint}}
        inlineButtons:
            { text: "Напоминания о работе с когнитивными искажениями", callback_data: "diary_reminder_settings" }
            { text: "Отправка статистики по дневнику", callback_data: "diary_stats_settings" }
    
    state: Callback
        event: telegramCallbackQuery || fromState = "/Settings/SettingChoice", onlyThisState = true
        if: $request.query == "diary_reminder_settings"
            go!: /Settings/DiaryReminderSetting
        elseif: $request.query == "diary_stats_settings"
            go!: /Settings/DiaryStatisticsSetting
    
    state: DiaryReminderSetting
        
        
    state: DiaryStatisticsSetting