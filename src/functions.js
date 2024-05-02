function sendCard(context, object, i) {
    log("obj " + object);
    log("index " + i);
    context.response.replies = context.response.replies || [];
    if (i < Object.keys(object).length) {
        context.response.replies.push({
            "type": "image",
            "imageUrl": object[Object.keys(object)[i]],
            "imageName": "<u>" + Object.keys(object)[i] + "</u>",
            "markup": "html"
        })
    }
    else {
        context.response.replies.push({
            "type": "text",
            "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.\n Введите команду /train.",
            "markup": "html"
        })
    }
}


function sendInlineButtons(context, buttonsNames) {
    context.response.replies = context.response.replies || [];
    log(buttonsNames)
    var buttons = [];
    log(buttons)
    for (var i = 0; i < buttonsNames.length; i++) {
        buttons.push({
          "text": buttonsNames[i],
          "callback_data": "inline_" + buttonsNames[i],
    })
    };
    log("buttons")
    log(toPrettyString(buttons))
    context.response.replies.push(
        {
        "type": "inlineButtons",
        "buttons": buttons
        }
    )
}


function sendMultipleCards(context, object1, object2, i) {
    log("obj " + object1);
    log("index " + i);
    context.response.replies = context.response.replies || [];
    
    if (i < Object.keys(object1).length) {
        context.response.replies.push({
          "type": "raw",
          "body": {
          "media": [
            {
            "type": "photo",
            "media": object1[Object.keys(object1)[i]],
            "caption": "<u>" + Object.keys(object1)[i] + "</u>",
            "parse_mode": "html"},
            {
            "type": "photo",
            "media": object2[Object.keys(object2)[i]],
            }
        ]},
          "method": "sendMediaGroup"
        })
    }
    else {
        context.response.replies.push({
            "type": "text",
            "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.\n Введите команду /train.",
            "markup": "html"
        })
    }
}