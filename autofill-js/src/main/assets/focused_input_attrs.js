(function () {
    var inputs = document.getElementsByTagName('input');

    for (var index = 0; index < inputs.length; ++index) {
        inputs[index].addEventListener('focus', (event) => {
            var attrsMap = event.target.attributes
            const attrs = Object.fromEntries(Array.from(attrsMap).map(item => [item.name, item.value]))
            Android.handleInputAttrs(JSON.stringify(attrs));
        }, true);
    }
})();