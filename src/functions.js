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
            "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.",
            "markup": "html"
        })
    }
}

