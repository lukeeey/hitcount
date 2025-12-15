const URL_REGEX = /[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/gi;

let socketState = "loading";

const handleSocket = () => {
    const socket = new WebSocket("wss://hitcount.dev/socket");
    const realtimeLoading = document.getElementById("realtime-loading");

    socket.onopen = () => {
        console.log("Connected to socket");
    }

    socket.onmessage = (e) => {
        if (socketState === "loading") {
            socketState = "loaded";
            realtimeLoading.style.display = "none";
        }
        const split = e.data.split(";");
        addToRealtimeUpdates(split[0], split[1]);
    }
}

const addToRealtimeUpdates = (path, count) => {
    const container = document.getElementById("realtime-container");
    container.innerHTML += (`
        <div class="realtime-update">
            <span class="realtime-date">${new Date().toLocaleString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }).replace(',', '')}</span>
            <a href="/p/${path}" class="realtime-path">${path}</a>
            <span class="realtime-count">${count}</span>
        </div>
    `);
}

const makeSvg = (styleValue, labelValue, labelColorValue, countColorValue) => {
    const countValue = 80;

    // Thank you ChatGPT
    const measureText = (text, fontSize = 11) => {
        const tempSvg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        const tempText = document.createElementNS("http://www.w3.org/2000/svg", "text");
        tempText.setAttribute("font-size", fontSize);
        tempText.setAttribute("font-family", "DejaVu Sans,Verdana,Geneva,sans-serif");
        tempText.textContent = text;
        tempSvg.appendChild(tempText);
        document.body.appendChild(tempSvg);
        const width = tempText.getBBox().width;
        document.body.removeChild(tempSvg);
        return width;
    };

    if (styleValue === "pixel") {
        return `<svg xmlns="http://www.w3.org/2000/svg" width="1" height="1"></svg>`;
    }

    const labelWidth = measureText(labelValue) + 10; 
    const countWidth = measureText(countValue) + 20; 
    const totalWidth = labelWidth + countWidth;

    // TODO
    switch (styleValue) {
        case "flat-rounded":
            break;
        case "for-the-badge":
            break;
    }
    
    return `
        <svg xmlns="http://www.w3.org/2000/svg" width="${totalWidth}" height="20" id="flat-square">
            <rect width="${labelWidth}" height="20" fill="${labelColorValue}" />
            <rect x="${labelWidth}" width="${countWidth}" height="20" fill="${countColorValue}" />
            <g fill="#fff" text-anchor="middle" font-size="11" font-family="DejaVu Sans,Verdana,Geneva,sans-serif">
                <text x="${labelWidth / 2}" y="14">${labelValue}</text>
                <text x="${labelWidth + countWidth / 2}" y="14">${countValue}</text>
            </g>
        </svg>
    `;
}

const updateBadge = () => {
    const styleElement = document.getElementById("style");
    const labelElement = document.getElementById("label");
    const labelColorElement = document.getElementById("label-color");
    const countColorElement = document.getElementById("count-color");

    document.getElementById("preview-badge").innerHTML = makeSvg(styleElement.value, labelElement.value, labelColorElement.value, countColorElement.value);
}

function generateBadge() {
    const usernameElement = document.getElementById("username");
    const repoElement = document.getElementById("repo");
    const labelColorElement = document.getElementById("label-color");
    const countColorElement = document.getElementById("count-color");
    const labelElement = document.getElementById("label");
    const styleElement = document.getElementById("style");
    const markdownContainer = document.getElementById("markdown-container");
    const htmlContainer = document.getElementById("html-container");
    const previewContainer = document.getElementById("preview-container");
    const errorContainer = document.getElementById("error-container");

    previewContainer.style.display = "none";
    errorContainer.style.display = "none";

    if (usernameElement.value === "" || usernameElement.value.length > 40) {
        errorContainer.style.display = "block";
        errorContainer.innerHTML = "Invalid username";
        return;
    }
    if (repoElement.value === "" || repoElement.value.length > 150) {
        errorContainer.style.display = "block";
        errorContainer.innerHTML = "Invalid repo name";
        return;
    }

    const baseUrl = `https://hitcount.dev/p/${usernameElement.value}/${repoElement.value}`;
    const svgUrl = new URL(baseUrl);

    if (svgUrl.pathname === "/" || svgUrl.pathname === "") {
        errorContainer.style.display = "block";
        errorContainer.innerHTML = "Invalid URL";
        return;
    }
    // Flat is default style so we don't need to include it in the url
    if (styleElement.value !== "flat") {
        svgUrl.searchParams.append("style", styleElement.value);
    }
    if (labelElement.value !== labelElement.getAttribute("placeholder")) {
        svgUrl.searchParams.append("label", labelElement.value);
    }
    if (labelColorElement.value !== labelColorElement.getAttribute("placeholder")) {
        svgUrl.searchParams.append("labelColor", labelColorElement.value.substring(1));
    }
    if (countColorElement.value !== countColorElement.getAttribute("placeholder")) {
        svgUrl.searchParams.append("countColor", countColorElement.value.substring(1));
    }

    const svgHref = svgUrl.origin + svgUrl.pathname + ".svg" + svgUrl.search;

    previewContainer.style.display = "block";
    markdownContainer.innerHTML = `[![Hits](${svgHref})](${baseUrl})`;
    htmlContainer.textContent = `<img src="${svgHref}" alt="Hit Counter">`;

    registerUrlType(`${usernameElement.value}/${repoElement.value}`);
}

const registerUrlType = async (path) => {
    await fetch("/registerPathData", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            path, urlType: 1
        })
    });
}

const getUrlType = (url) => {
    switch(url.hostname) {
        case "github.com": 
            return 1;
        case "gitlab.com":
            return 2;
    }
    return 0;
}

// This could probably be done better but this is what the library author does
// https://taufik-nurrohman.js.org/color-picker/tweak/replace.html
function replaceWithColorBox(source) {
    if (source.hasColorBox) {
        return;
    }
    source.hasColorBox = true;
    let box = document.createElement("span"),
        color = source.value;
    box.value = color; // Hacky :(
    box.style.backgroundColor = color;
    source.parentNode.insertBefore(box, source);
    source.type = "hidden";
    const picker = new CP(box);
    box.className = picker.state.class;
    picker.on("change", function (r, g, b, a) {
        let color = this.color(r, g, b, a);
        source.value = color;
        this.source.value = color; // Hacky :(
        this.source.style.backgroundColor = color;

        updateBadge();
    });
}

document.getElementById("label").oninput = updateBadge;
document.getElementById("style").onchange = updateBadge;
document.getElementById("generate-btn").onclick = generateBadge;

replaceWithColorBox(document.getElementById("label-color"));
replaceWithColorBox(document.getElementById("count-color"));

updateBadge();
handleSocket();