theme: /Settings
    
    state: SettingChoice
        q!: $regex</settings>
        a: {{contents.setting_hint}}
        inlineButtons:
            { text: "Настройка напоминаний о работе с когнитивными искажениями", callback_data: "diary_reminder_settings" }
            { text: "Настройка отправки статистики по дневнику", callback_data: "diary_stats_settings" }
 