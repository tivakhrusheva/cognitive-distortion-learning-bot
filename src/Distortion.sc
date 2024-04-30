require: functions.js
require: ./data/contents.yaml
    var = contents
require: ./data/distortions.js

theme: /Distortion
    
    state: DistortionBegin
        q!: $regex</learn>
        a: Готовы начать знакомство с когнитивными искажениями?
        inlineButtons:
            {text: "Да", callback_data: "Distortion_ready"}
            {text: "Нет", callback_data: "Distortion_not_ready"}
     
    
    state: DistortionCard
        q: Да || fromState = "/Distortion/DistortionBegin"
        q: Дальше || fromState = "/Distortion/DistortionCard", onlyThisState = true
        script:
            $client.cardNumber = $client.cardNumber+=1 || 0;
            sendCard($context, urls, $client.cardNumber)
        inlineButtons:
            {text: "Дальше", callback_data: "Distortion_next"}
            {text: "Назад в меню", callback_data: "Distortion_back_to_menu"}
                