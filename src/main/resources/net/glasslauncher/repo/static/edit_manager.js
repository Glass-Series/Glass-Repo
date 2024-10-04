/* LICENSE:
This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

tl;dr: Feel free to use this code in your own projects, so long as you mention me (calmilamsy) somewhere and don't use my code to make money by selling shit.
 */

/*
This script is pretty awkward though, so good luck hooking it into your own website if you choose to.
 */

function addDivs() {
    return fetch("/repo/api/validmodvalues")
        .then(response => response.json())
        .then((jsonData) => {
            MOD_VALID_VALUES = jsonData["valid_values"];
            mod_fields = jsonData["fields"];
            mod_descriptions = jsonData["descriptions"];
            mod_formats = jsonData["formats"];

            let html = "";
            Object.keys(mod_fields).forEach(function(entry) {
                html += `<div id=\"${entry.split(" ")[0]}_parent_div\"></div>`
            });
            document.getElementById("form").innerHTML += html + `
                <br>
                <button class="btn btn-success" >Submit</button>
            `;
        })
        .catch((error) => {
            console.error(error);
            alert("WARNING: Failed to valid values for your mod! Try refreshing.\nIf this error persists, alert someone in the Glass Series Discord and DO NOT TRY TO EDIT YOUR MOD.\n\nError: " + error)
        });
}

function getDefualts() {
    let parts = window.location.toString().split('/');
    parts.pop() || parts.pop();
    return fetch("/repo/api/mod/" + parts.pop())
        .then(response => response.json())
        .then((jsonData) => {
            mod_json = jsonData;
        })
        .catch((error) => {
            console.error(error);
            alert("WARNING: Failed to load defaults for your mod! Try refreshing.\nIf this error persists, alert someone in the Glass Series Discord.\n\nError: " + error)
        });
}

/**
 * @param {string} input
 */
function setupEdits(input) {
    let field = "";
    try {
        field = mod_fields[input];
    } catch (e) {
        field = null;
    }
    if (field != null && mod_active[input] !== true) {
        let element = document.getElementById(`${input.split(" ")[0]}_parent_div`);
        mod_active[input] = true;
        if (field.endsWith("[")) {
            if (mod_formats[input].startsWith("!")) {
                let extracrap = "";
                let readonly = true;
                if (mod_formats[input].startsWith("!categories")) {
                    for (let index in MOD_VALID_VALUES["categories"]) {
                        extracrap += `
                            <option>${MOD_VALID_VALUES["categories"][index]}</option>
                        `
                    }
                }
                else if (mod_formats[input].startsWith("!types")) {
                    for (let index in MOD_VALID_VALUES["types"]) {
                        extracrap += `
                            <option>${MOD_VALID_VALUES["types"][index]}</option>
                        `
                    }
                }
                else if (mod_formats[input].startsWith("!versions")) {
                    for (let index in MOD_VALID_VALUES["minecraft_versions"]) {
                        extracrap += `
                            <option>${MOD_VALID_VALUES["minecraft_versions"][index]}</option>
                        `
                    }
                }
                else if (mod_formats[input].startsWith("!urls")) {
                    console.log("URL");
                    let asd = "^(http:\\/\\/|https:\\/\\/)+[^\\s]+[\\w]$";
                    extracrap = `pattern=\"${asd}\"`;
                    setupArray(element, field.replace("[", ""), input, extracrap);
                    setArrayContents(input, field);
                    return;
                }
                console.log(mod_formats);
                setupArrayHandler(element, field.replace("[", ""), input, extracrap);
                setArrayContents(input, field, readonly);
            }
            else {
                setupArray(element, field.replace("[", ""), input, "");
                setArrayContents(input, field);
            }
        }
        else {
            if (mod_formats[input].startsWith("!")) {
                if (mod_formats[input] === "!markdown") {
                    setupMarkdown(element, field, input);
                    setContents(element, field, true);
                }
            }
            else {
                setup(element, field, input);
                setContents(element, field);
            }
        }
    }
}

/**
 * @param {string} field
 * @param {string} name
 * @param {boolean} readonly
 */
function setArrayContents(name, field, readonly = false) {
    let extracrap = "";
    if (readonly === true) {
        extracrap = "readonly";
    }
    let authors = mod_json[field.replace("[", "")];
    for (let value in authors) {
        document.getElementById(`${name.split(" ")[0]}_div`).innerHTML += `
            <div>
                <br style="font-size: 2px;">
                <div class="input-group">
                    <input class="form-control" type="text" name="${field.replace("[", "")}" value="${authors[value]}" ${extracrap}>
                    <div class="input-group-append"></div>
                    <div class="input-group-text" onclick="this.parentElement.parentElement.remove()" style="background-color: #400001; border-color: #1f0000"><div class="fa fa-trash" style="color: red;"></div></div>
                </div>
            </div>
        `
    }
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {boolean} ismarkdown
 */
function setContents(element, field, ismarkdown = false) {
    if (ismarkdown === true) {
        markdowneditor.value(mod_json[field.replace("[", "")])
    }
    else {
        element.getElementsByTagName("input")[0].value = mod_json[field.replace("[", "")];
    }
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 * @param {string} extracrap
 */
function setupArray(element, field, name, extracrap) {
    element.innerHTML += `
        <div class="form-group" id="${name}">
            <br>
            <label><b>${name}</b></label> <span>${mod_descriptions[name]}</span>
            <div class="input-group-text" onclick="removeSection(this.parentElement);" style="background-color: #400001; border-color: #1f0000; float: right;"><div class="fa fa-trash" style="color: red;"></div></div>
            <br>
            <div class="btn btn-success" onclick="addElement(document.getElementById('${name.split(" ")[0]}_parent_div'), '${field}', '${name}', null, '`+escape(extracrap)+`')">Add</div>
            <br style="font-size: 2px;">
            <div id="${name.split(" ")[0]}_div">
            </div>
        </div>
    `;
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 * @param {string} extracrap
 */
function setupArrayHandler(element, field, name, extracrap) {
    element.innerHTML += `
        <div class="form-group" id="${name}">
            <br>
            <label><b>${name}</b></label> <span>${mod_descriptions[name]}</span>
            <div class="input-group-text" onclick="removeSection(this.parentElement);" style="background-color: #400001; border-color: #1f0000; float: right;"><div class="fa fa-trash" style="color: red;"></div></div>
            <br>
            <select id="${name.split(" ")[0]}_picker">
                ${extracrap}
            </select>
            <div class="btn btn-success" onclick="addElement(document.getElementById('${name.split(" ")[0]}_parent_div'), '${field}', '${name}', document.getElementById('${name.split(" ")[0]}_picker'))">Add</div>
            <br style="font-size: 2px;">
            <div id="${name.split(" ")[0]}_div">
            </div>
        </div>
    `;
    $("#" + name.split(" ")[0] + "_picker").selectpicker({liveSearch: true})
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 * @param {Element} extracrap
 * @param {string} extrastringcrap
 */
function addElement(element, field, name, extracrap = null, extrastringcrap = "") {
    let values = [];
    Array.prototype.forEach.call(element.getElementsByTagName("input"), function (input) {
        values.push(input.value);
    });
    if (extracrap !== null) {
        if (values.includes(extracrap.value)) {
            return;
        }
        extracrap = `readonly value=\"${extracrap.value}\"`;
        extracrap.value = null;
    }
    document.getElementById(`${name.split(" ")[0]}_div`).innerHTML += `
        <div>
            <br style="font-size: 2px;">
            <div class="input-group">
                <input class="form-control" type="text" name="${field}" ${extracrap} ${unescape(extrastringcrap)}>
                <div class="input-group-append"></div>
                <div class="input-group-text" onclick="this.parentElement.parentElement.remove()" style="background-color: #400001; border-color: #1f0000"><div class="fa fa-trash" style="color: red;"></div></div>
            </div>
        </div>
    `;
    let elements = element.getElementsByTagName("input");
    for (let i = values.length; i !== -1; i--) {
        if (values[i] !== undefined) {
            elements[i].value = values[i];
        }
    }
}

/**
 * @param {Element} element
 */
function removeSection(element) {
    let name = element.getElementsByTagName("label")[0].innerText;
    mod_active[name] = false;
    document.getElementById(name).remove();
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 */
function setup(element, field, name) {
    element.innerHTML += `
        <div class="form-group" id="${name}">
            <br>
            <label><b>${name}</b></label> <span>${mod_descriptions[name]}</span>
            <div class="input-group-text" onclick="removeSection(document.getElementById('${name.split(" ")[0]}_parent_div'));" style="background-color: #400001; border-color: #1f0000; float: right;"><div class="fa fa-trash" style="color: red;"></div></div>
            <input class="form-control" type="text" name="${field}">
        </div>
    `;
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 */
function setupMarkdown(element, field, name) {
    element.innerHTML += `
        <div class="form-group" id="${name}">
            <br>
            <label><b>${name}</b></label> <span>${mod_descriptions[name]} <a class="btn btn-secondary btn-sm rounded-lg author" href="#" onmouseup="downloadtext(markdowneditor.value(), 'mod_markdown.md')">Download Markdown</a></span>
            <div class="input-group-text" onclick="removeSection(document.getElementById('${name.split(" ")[0]}_parent_div'));" style="background-color: #400001; border-color: #1f0000; float: right;"><div class="fa fa-trash" style="color: red;"></div></div>
            <textarea id="markdowneditor" class="form-control" name="${field}">
        </div>
    `;
    markdowneditor = new EasyMDE({
        element: document.getElementById("markdowneditor"),
        forceSync: true,
        status: ["autosave", "lines", "words", "cursor", {
            className: "charcount",
            defaultValue: function(el) {
                el.innerHTML = document.getElementById("markdowneditor").value.length + " chars";
            },
            onUpdate: function(el) {
                el.innerHTML = document.getElementById("markdowneditor").value.length + " chars";
            }
        }]
    });
}

function submitFormAsJSONCauseIHateHowThisWorks() {
//    XMLHttpRequest()
//    $.ajax({
//        type: "POST",
//        url: document.documentURI,
//        data: JSON.stringify($("#form").serializeArray())
//    });
    var xhr = new XMLHttpRequest();
    var url = document.documentURI;
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    var data = JSON.stringify($("#form").serializeArray());
    xhr.send(data);
}

/**
 * @type {Object.<string, string>}
 */
mod_fields = {
};

/**
 * @type {Object.<string, string>}
 */
mod_formats = {
};

/**
 * @type {Object.<string, boolean>}
 */
mod_active = {
};

/**
 * @type {Object.<string, string>}
 */
mod_descriptions = {
};

/**
 * @type {Object.<string, Object.<string>>}
 */
let MOD_VALID_VALUES = {
};

mod_json = null;
markdowneditor = null;