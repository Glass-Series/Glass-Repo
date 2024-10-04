//!! GLASS REPO LEGACY CODE, TO BE REPLACED !!//

// Credit to https://blog.praveen.science/natural-sorting-in-javascript for the alnum sorting code.

let iDontKnowWhyButThisFixedMyWoes = new Intl.Collator(undefined, {
  numeric: true,
  sensitivity: "base"
});

function jsDoesntSortProperlySoFineIWillDoItMyself(a, b, doReverse) {
    if (a !== b) {
        return [a, b].sort(iDontKnowWhyButThisFixedMyWoes.compare)[doReverse ? 1 : 0] === a;
    }
    return false;
}

/**
 * @param {string} thingToFilter
 * @param {boolean} dir true = asc
 */
function sortTable(thingToFilter, dir = true) {
    let table, rows, switching, i, x, y, shouldSwitch, direction, switchcount = 0;
    table = document.getElementsByClassName("searchabletable")[0];
    switching = true;
    direction = dir;
    // Set the sorting direction to ascending:
    /* Make a loop that will continue until
    no switching has been done: */
    while (switching) {
        // Start by saying: no switching is done:
        switching = false;
        rows = table.getElementsByClassName("modcontainer");
        /* Loop through all table rows (except the
        first, which contains table headers): */
        for (i = 1; i < rows.length; i++) {
            // Start by saying there should be no switching:
            shouldSwitch = false;
            /* Get the two elements you want to compare,
            one from current row and one from the next: */
            x = rows[i - 1].getElementsByClassName(thingToFilter.replace("!", ""))[0];
            y = rows[i].getElementsByClassName(thingToFilter.replace("!", ""))[0];
            /* Check if the two rows should switch place,
            based on the direction, asc or desc: */
            let textContentX = "";
            let textContentY = "";
            if (thingToFilter.startsWith("!")) {
                textContentX = x.title;
                textContentY = y.title;
            }
            else {
                textContentX = x.textContent;
                textContentY = y.textContent;
            }

            console.log(thingToFilter);
            if (["downloads", "versiontime", "time"].includes(thingToFilter.replace("!", ""))) {
                shouldSwitch = jsDoesntSortProperlySoFineIWillDoItMyself(textContentX, textContentY, direction);
                if (shouldSwitch) {
                    break;
                }
            }
            else if (direction === true) {
                if (textContentX.toLowerCase() > textContentY.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            }
            else if (direction === false) {
                if (textContentX.toLowerCase() < textContentY.toLowerCase()) {
                    // If so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            /* If a switch has been marked, make the switch
            and mark that a switch has been done: */
            rows[i - 1].parentNode.insertBefore(rows[i], rows[i - 1]);
            switching = true;
            // Each time a switch is done, increase this count by 1:
            switchcount++;
            if (switchcount > 1000) {
                stop();
            }
        } else {
            /* If no switching has been done AND the direction is "asc",
            set the direction to "desc" and run the while loop again. */
            if (switchcount === 0 && direction === dir) {
                direction = !direction;
                switching = true;
            }
        }
    }
}

/**
 * Holy shit this is bad, and what makes it even worse is it doesnt even work properly.
 * @param {Element} table
 */
function runSearch(table) {
    // Declare variables
    let input, filter, ul, li, tr, i, txtValue, filterUpper, isRegex, td, isAnd, pickers;
    input = table.getElementsByClassName("searchabletableinput")[0];
    filter = input.value;
    filterUpper = filter.toUpperCase();
    isRegex = document.getElementById("isRegex").checked;
    isAnd = document.getElementById("isAnd").checked;
    ul = table.getElementsByClassName("searchabletable")[0];
    li = ul.getElementsByClassName("modcontainer");
    pickers = ["Types", "Minecraft", "Categories", "Tags"];

    // Loop through all list items, and hide those who don't match the search query
    for (i = 0; i < li.length; i++) {
        tr = li[i];
        td = tr.getElementsByTagName("div")[0];
        txtValue = td.textContent || td.innerText;
        let isMatch = false;
        if (isRegex) {
            if (txtValue.search(filter) !== -1) {
                isMatch = true;
            }
        } else {
            if (txtValue.toUpperCase().indexOf(filterUpper) > -1) {
                isMatch = true;
            }
        }
        if (!isAnd) {
            for (let picker = pickers.length; picker !== 0; picker--) {
                let val;
                if (pickers[picker - 1] !== "Tags") {
                    val = $("#" + pickers[picker - 1] + "_picker").val();
                } else {
                    val = $("#Tags_div")[0];
                    let newval = [];
                    let inputs = val.getElementsByTagName("input");
                    for (let tag = inputs.length; tag !== 0; tag--) {
                        newval.push(inputs[tag - 1].value);
                    }
                    val = newval;
                }

                let btns = tr.getElementsByClassName(pickers[picker - 1])[0].getElementsByTagName("button");
                if (val.length !== 0) {
                    for (let btn = btns.length; btn !== 0; btn--) {
                        if (val.includes(btns[btn - 1].value)) {
                            isMatch = true;
                        } else {
                            isMatch = false;
                        }
                    }
                } else {
                    isMatch = true;
                }
            }
        } else if (isAnd && isMatch) {
            for (let picker = pickers.length; picker !== 0; picker--) {
                let btns = tr.getElementsByClassName(pickers[picker - 1])[0].getElementsByTagName("button");
                let vals;
                if (pickers[picker - 1] !== "Tags") {
                    vals = $("#" + pickers[picker - 1] + "_picker").val();
                } else {
                    vals = $("#Tags_div")[0];
                    let newval = [];
                    let inputs = vals.getElementsByTagName("input");
                    for (let tag = inputs.length; tag !== 0; tag--) {
                        newval.push(inputs[tag - 1].value);
                    }
                    vals = newval;
                }
                if (btns.length !== 0 && vals.length !== 0) {
                    let btnvals = [];
                    for (let val = btns.length; val !== 0; val--) {
                        btnvals.push(btns[val - 1].value);
                    }
                    for (let val = vals.length; val !== 0; val--) {
                        if (btnvals.includes(vals[val - 1])) {
                            isMatch = true;
                        } else {
                            isMatch = false;
                            break;
                        }
                    }
                    if (pickers[picker] === "Tags") {
                    }
                    if (btnvals.length === 0) {
                        isMatch = false;
                    }
                    if (!isMatch) {
                        break
                    }
                } else if (btns.length === 0 && vals.length !== 0) {
                    isMatch = false;
                }
            }
        }
        if (isMatch) {
            tr.style.display = "";
        } else {
            tr.style.display = "none";
        }
    }
}

function setup() {
    fetch("/repo/api/validmodvalues")
        .then(response => response.json())
        .then((jsonData) => {
            MOD_VALID_VALUES = jsonData["valid_values"];
            let stuff = ["Categories", "Minecraft Versions", "Types"];
            for (let thing in stuff) {
                thing = stuff[thing];
                let extracrap = "";
                let thing_snakeified = thing.toLowerCase().replace(" ", "_");
                for (let index in MOD_VALID_VALUES[thing_snakeified]) {
                    extracrap += `
                        <option>${MOD_VALID_VALUES[thing_snakeified][index]}</option>
                    `
                }
                setupArrayHandler(document.getElementById(thing_snakeified + "_parent_div"), "", thing, extracrap, "multiple data-selected-text-format=\"count > 3\"");
            }
            stuff = ["Tags"];
            for (let thing in stuff) {
                thing = stuff[thing];
                let thing_snakeified = thing.toLowerCase().replace(" ", "_");
                setupArray(document.getElementById(thing_snakeified + "_parent_div"), "", thing);
            }
        })
        .catch((error) => {
            console.error(error);
            alert("WARNING: Failed to fetch predefined values for searching! Try refreshing.\nIf this error persists, alert someone in the Glass Series Discord.\n\nError: " + error)
        });
    return true;
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 */
function setupArray(element, field, name) {
    element.innerHTML += `
        <div class="form-group" id="${name}">
            <label>${name}</label>
            <input class="input-group-text form-control-sm" type="text" id="${name.split(" ")[0]}_input" onsubmit="addElement(document.getElementById('${name.split(" ")[0]}_div'), '${field}', '${name}', this.value)">
            <br style="font-size: 2px;">
            <div class="row" style="width: 236px;">
                <div class="btn btn-sm btn-success" style="margin-left: 15px; margin-right: 2px; width: 45px!important;" onclick="addElement(document.getElementById('${name.split(" ")[0]}_div'), '${field}', '${name}', document.getElementById('${name.split(" ")[0]}_input'))">Add</div>
                <div class="dropdown" style="width: 174px;">
                    <button class="btn btn-sm btn-secondary dropdown-toggle" style="width: 179px;" type="button" id="toggle_${name}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Selected Tags</button>
                    <div class="dropdown-menu" id="${name.split(" ")[0]}_div" aria-labelledby="toggle_${name}">
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 * @param {string} extracrap
 * @param {string} extraextracrap
 */
function setupArrayHandler(element, field, name, extracrap, extraextracrap = "") {
    element.innerHTML += `
        <div id="${name}">
            <label>${name}</label>
            <br>
            <select id="${name.split(" ")[0]}_picker" ${extraextracrap}>
                ${extracrap}
            </select>
        </div>
    `;
    let elem = $("#" + name.split(" ")[0] + "_picker");
    elem.selectpicker({liveSearch: true, multiple: true});
    elem.selectpicker("setStyle", "btn-sm btn", "add");
}

/**
 * @param {Element} element
 * @param {string} field
 * @param {string} name
 * @param {Element} extracrap
 */
function addElement(element, field, name, extracrap = null) {
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
        <div style="margin-left: 10px; margin-right: 10px;">
            <br style="font-size: 2px;">
            <div class="input-group">
                <input class="input-group-text w-75" type="text" name="${field}" ${extracrap}>
                <div class="input-group-append w-25">
                    <div class="input-group-text" onclick="this.parentElement.parentElement.parentElement.remove()" style="background-color: #400001; border-color: #1f0000"><div class="fa fa-trash" style="color: red;"></div></div>
                </div>
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
 * @type {Object.<string, Object.<string>>}
 */
let MOD_VALID_VALUES = {
    "minecraft_versions": [],
    "types": [],
    "categories": []
};