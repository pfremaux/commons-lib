/*
Each entity must declare a structure with this format
let structure = {
    "<entity name>" : {
        "<ID input text>":"<text/label of this input>",
        ...
    }
};
*/

function buildRequestBody(struct) {
    let json = {};
    let definition = struct[entity];
    for (let k in definition) {
        json[k] = get(k).value;
    }
    return json;
}

function get(id) {
    return document.getElementById(id);
}

function hideAllExcept(baseId, idx) {
    console.log("idx = " + idx + " = " + get(baseId+idx));
    let obj;
    for (let i = 0 ; (obj = get(baseId+i)) !== null ; i++) {
        console.log("obj = " + obj + " with id = " + baseId+i);
        let display = 'none';
        if (idx === i && obj.style.display !== 'block') {
            display = 'block';
        }
        console.log("display = " + display);
        obj.style.display = display;
    }
}

function title(parentNode, level, txt) {
    let h = create("h" + level);
    h.innerHTML = txt;
    parentNode.appendChild(h);
}

function create(name) {
    return document.createElement(name);
}

function when(e, evtType, fn) {
    e.addEventListener(evtType, fn);
}

function newLine(target) {
    target.appendChild(create("br"));
}

function option(txt) {
    let o = create("option");
    o.value = txt;
    o.innerHTML = txt;
    return o;
}

function label(parentNode, forId, label) {
    let l = create("label");
    l.innerHTML = label;
    l.for = forId;
    parentNode.appendChild(l);
    return l;
}

function select(parentNode, id, label, data, evt) {
    let l = create("label");
    l.innerHTML = label;
    l.for = id;
    parentNode.appendChild(l);
    let s = create("select");
    s.id = id;
    data.forEach(element => {
        s.appendChild(option(element));
    });
    when(s, "change", evt);
    parentNode.appendChild(s);
    newLine(parentNode);
    return s;
}

function addSelectData(id, txt) {
    get(id).appendChild(option(txt));
}

function text(parentNode, id, lbl, value, evt) {
    label(parentNode, id, lbl);
    let i = create("input");
    i.id = id;
    i.type = "text";
    i.value = value;
    if (evt) {
        i.addEventListener("change", evt);
    }
    parentNode.appendChild(i);
    newLine(parentNode);
    return i;
}

function area(parentNode, id, lbl, value, evt) {
    label(parentNode, id, lbl);
    let i = create("textarea");
    i.id = id;
    i.innerHTML = value;
    if (evt) {
        i.addEventListener("change", evt);
    }
    parentNode.appendChild(i);
    newLine(parentNode);
    return i;
}

function p(parentNode) {
    let b = create("p");
    parentNode.appendChild(b);
    return b;
}

function button(parentNode, id, label, evt) {
    let b = create("button");
    b.id = id;
    b.innerHTML = label;
    when(b, "click", evt);
    parentNode.appendChild(b);
    newLine(parentNode);
    return b;
}

function async(method, path, callback, data, headers) {
    var xhttp = new XMLHttpRequest();
    // xhttp.setRequestHeader(header, value); TODO PFR
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        callback(this.responseText);
      }
    };
    xhttp.open(method, path, true);
    if (headers) {
        for (let k in headers) {
            xhttp.setRequestHeader(k, headers[k]);
        }
    }
    //xhttp.setRequestHeader("sec", "application/x-www-form-urlencoded");
    xhttp.send(data);
}

function asyncGet(path, fn) {
    var xhttp = new XMLHttpRequest();
    // xhttp.setRequestHeader(header, value); TODO PFR
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        fn(this.responseText);
      }
    };
    xhttp.open("GET", path, true);
    xhttp.send();
}

function capitalize (value) {
  var textArray = value.split(' ')
  var capitalizedText = ''
  for (var i = 0; i < textArray.length; i++) {
    capitalizedText += textArray[i].charAt(0).toUpperCase() + textArray[i].slice(1) + ' '
  }
  return capitalizedText.substring(0, capitalizedText.length - 1);
}

function table(parentNode, data, entity, allowUpdate, allowDelete) {
    console.log(allowUpdate);
    let t = create("table");
    // HEADER
    let headerData = Object.keys(data[0]);
    let thead = t.createTHead();
    let row = thead.insertRow();
    for (let key of headerData) {
        let th = create("th");
        let text = document.createTextNode(key);
        th.appendChild(text);
        row.appendChild(th);
    }
    if (allowUpdate) {
        let th = create("th");
        let text = document.createTextNode("Update");
        th.appendChild(text);
        row.appendChild(th);
    }
    if (allowDelete) {
        let th = create("th");
        let text = document.createTextNode("Delete");
        th.appendChild(text);
        row.appendChild(th);
    }
    // DATA
    for (let element of data) {
        let row = t.insertRow();
        for (key in element) {
            let cell = row.insertCell();
            if (typeof element[key] === 'string') {
                let text = document.createTextNode(element[key]);
                cell.appendChild(text);
            } else {
                cell.appendChild(element[key]);
            }
        }
        if (allowUpdate) {
            let cell = row.insertCell();
            let l = link(cell, "object.html?entity="+entity+"&entityId=" + element["id"+capitalize(entity)], "edit");
            // let l = link(cell,  "/entities/" + entity + "/" + element["id"+capitalize(entity)], "edit");
            //let l = button(cell, "idUpdateTODOUniq", "Update", e => async("PUT", "/entities/" + entity + "/" + element["id"+entity], r => console.log(r), ));
            // cell.appendChild(text);
        }
        if (allowDelete) {
            let cell = row.insertCell();
            // todo pfr
            cell.appendChild(text);
        }
      }
      parentNode.appendChild(t);
      return t;
}

function link(parentNode, url, lbl) {
    let a = create("a");
    a.href = url;
    a.innerHTML = lbl;
    parentNode.appendChild(a);
    return a;
}

function asyncLink(parentNode, url, lbl, actionFunction) {
    let a = create("a");
    a.href = "#";
    a.addEventListener("click", () => asyncGet(url, actionFunction));
    a.innerHTML = lbl;
    parentNode.appendChild(a);
    return a;
}

function img(parentNode, src) {
    let img = create("img");
    img.src = src;
    parentNode.appendChild(img);
    return img;
}

function span(parentNode, id, classStyle) {
    let d = create("span");
    if (id) {
        d.id = id;
    }
    if (classStyle) {
        d.className = classStyle;
    }
    parentNode.appendChild(d);
    return d;
}

function div(parentNode, id, classStyle) {
    let d = create("div");
    if (id) {
        d.id = id;
    }
    if (classStyle) {
        d.className = classStyle;
    }
    parentNode.appendChild(d);
    return d;
}

function video(parentNode, id, fileExtension, urlPath) {
    let v = create("video");
    if (id) {
        v.id = id;
    }
    v.controls = true;

    let s = create("source");
    s.src=urlPath;
    if (fileExtension === 'mp4') {
        s.codecs="H.264/MPEG-4";
    }
    s.preload="metadata";
    s.type="video/"+fileExtension;
    v.appendChild(s);
    parentNode.appendChild(v);
    return v;
}

/*
<video id="vid" width="320" height="240" controls>
  <source autoplay="autoplay" preload="metadata" src="http://127.0.0.1:8080/downloader/godl/test.mp4" type="video/mp4"  codecs="H.264/MPEG-4">
<!--  <source src="file:///c:/Users/Pierre/Videos/secr.mp4" type="video/ogg"> -->
Your browser does not support the video tag.
</video>
*/

//  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
/*
let f = get("editor");
newLine(f);
newLine(f);

select(f, "idSel", "actions", ["run", "walk", "jump"], e => console.log("changed"));
button(f, "idBtn", "go", e => console.log(e));
//button(f, "idBtn", "go", e => console.log(e));
async("GET", "/private/selfDescribe/", txt => console.log(txt));*/