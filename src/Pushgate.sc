theme: /Pushgate
    
    state: CreateReminder
        script:
            var pushback = $pushgate.createEvent(
                $request.channelType,
                $request.botId,
                $request.channelUserId,
                "DiaryNotification",
                {{"text": "Скидка только для вас!"}}
            );
                                
    state: AlterReminder
        
        
    state: DeleteReminder
        script:
            $pushgate.cancelEvent($cient.StatisticsNotificationId);
            delete $cient.StatisticsNotificationId;