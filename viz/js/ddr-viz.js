const BLANK = "_";

const FONT_ID = "10px Times";
const FONT_FORM = "bold 13px Times";
const FONT_LEMMA = "13px Times";
const FONT_POS = "12px Times";
const FONT_SEM = "12px Times";
const FONT_DEPREL = "12px Times";

const ID_GAP_W = 1;
const ID_GAP_H = 2;
const FORM_GAP_W = 30;
const FORM_GAP_H = 12;
const LEXICON_GAP_H = 20;

const COLOR_POS = "#245599";
const COLOR_SEM = "#DC143C";
const COLOR_EDGE = "#808080";
const COLOR_DEPREL = "#006400";

const ANCHOR_W = 4;
const ANCHOR_H = 8;
const ARC_RADIUS = 5;
const ARC_GAP_H = 20;
const ARC_GAP_W = ARC_RADIUS * 2;

// const DEPREL_MARGIN_W = 40;
const DEPREL_SHIFT_W = 4;
const DEPREL_SHIFT_H = 4;

const INIT_X = 25;
const INIT_Y = 40;

let D_GRAPHS = [];
let INPUT_FORMAT = "json";

// ======================================== Dependency ========================================

class DEPArc {
    constructor(node, label) {
        this.set(node, label);
    }

    set(node, label) {
        this.node = node;
        this.label = label;
    }
}

class DEPNode {
    constructor() {
        this.id = 0;
        this.form = "root";
        this.lemma = "root";
        this.pos = BLANK;
        this.ner = BLANK;
        this.feats = new Map();
        this.head = new DEPArc(null, null);
        this.s_heads = [];
    }

    get parent() {
        return this.head.node;
    }

    get deprel() {
        return this.head.label;
    }

    get semtag() {
        return this.feats.has("sem") ? this.feats.get("sem") : "";
    }
}

class DEPGraph {
    constructor() {
        this.nodes = [new DEPNode()];
        this.r_lexica = null;
        this.r_edges = null;
        this.r_s_edges = null;
        this.max_height = 0;
    }

    get(index) {
        return this.nodes[index];
    }

    add(node) {
        this.nodes.push(node);
    }

    get size() {
        return this.nodes.length;
    }
}

function initTSV(input) {
    function initFeatMap(node, fs) {
        if (fs === BLANK) return;
        let feats = fs.split("|");

        for (let i = 0; i < feats.length; i++) {
            let t = feats[i].split("=");
            node.feats.set(t[0], t[1].toUpperCase());
        }
    }

    function initPrimaryDependency(node, parent, label) {
        node.head.set(parent, label);
    }

    function initSecondaryDependencies(graph, node, fs) {
        if (fs === BLANK) return;
        let heads = fs.split("|");

        for (let i = 0; i < heads.length; i++) {
            let t = heads[i].split(":");
            let head = graph.get(parseInt(t[0]));
            node.s_heads.push(new DEPArc(head, t[1]));
        }
    }

    function initGraph(fields) {
        let graph = new DEPGraph();

        for (let i = 0; i < fields.length; i++)
            graph.add(new DEPNode());

        for (let i = 0; i < fields.length; i++) {
            let node = graph.get(i + 1);
            let f = fields[i];

            node.id = parseInt(f[0]);
            node.form = f[1];
            node.lemma = f[2];
            node.pos = f[3];
            initFeatMap(node, f[4]);
            initPrimaryDependency(node, graph.get(parseInt(f[5])), f[6]);
            if (f.length > 7) initSecondaryDependencies(graph, node, f[7]);
            node.ner = (f.length > 8 && f[8].length > 1) ? f[8] : BLANK;
        }

        return graph;
    }

    let lines = input.split("\n");
    let graphs = [];
    let fields = [];

    for (let i = 0; i < lines.length; i++) {
        let f = lines[i].trim().split("\t");

        if (f.length < 7) {
            graphs.push(initGraph(fields));
            fields = [];
        }
        else {
            fields.push(f);
        }
    }

    if (fields.length > 0)
        graphs.push(initGraph(fields));

    return graphs;
}

function initJSON(input) {
    let doc = JSON.parse(input);
    let graphs = [];

    if (doc.hasOwnProperty("output"))
        doc = doc["output"];

    doc.forEach(function (sentence) {
        let forms = sentence["tok"];
        let graph = new DEPGraph();
        graphs.push(graph);

        for (let i = 0; i < forms.length; i++) {
            let node = new DEPNode();
            node.id = i + 1;
            node.form = forms[i];
            graph.add(node);
        }

        if (sentence.hasOwnProperty("lem")) {
            let lemmas = sentence["lem"];
            for (let i = 0; i < lemmas.length; i++)
                graph.get(i + 1).lemma = lemmas[i];
        }

        if (sentence.hasOwnProperty("pos")) {
            let poss = sentence["pos"];
            for (let i = 0; i < poss.length; i++)
                graph.get(i + 1).pos = poss[i];
        }

        if (sentence.hasOwnProperty("ner")) {
            sentence["ner"].forEach(function (c) {
                for (let i = c[0]; i < c[1]; i++)
                    graph.get(i + 1).ner = 'N-' + c[2];
            });
        }

        if (sentence.hasOwnProperty("dep")) {
            let deps = sentence["dep"];
            for (let i = 0; i < deps.length; i++) {
                let node = graph.get(i + 1);
                let head = graph.get(deps[i][0] + 1);
                node.head.set(head, deps[i][1]);
            }
        }

        if (sentence.hasOwnProperty("dep2")) {
            sentence["dep2"].forEach(function (c) {
                graph.get(c[0]+1).s_heads.push(new DEPArc(graph.get(c[1]+1), c[2]));
            });
        }
    });

    return graphs;
}

// ======================================== Lexicon ========================================

class Lexicon {
    constructor(ctx, node) {
        function getFontWidth(font, str) {
            ctx.beginPath();
            ctx.font = font;
            let width = ctx.measureText(str).width;
            ctx.closePath();
            return width;
        }

        this.x_min = 0;
        this.w_form = getFontWidth(FONT_FORM, node.form);
        // this.w_lemma = getFontWidth(FONT_LEMMA, node.lemma);
        this.w_pos = getFontWidth(FONT_POS, node.pos);
        this.w_deprel = getFontWidth(FONT_DEPREL, node.deprel);
        // this.w_sem = getFontWidth(FONT_SEM, node.semtag);
        this.w_max = Math.max(this.w_form, this.w_pos);
        this.w_s_deprels = node.s_heads.map(function (x) {
            return getFontWidth(FONT_DEPREL, x.label);
        });
    }

    get x_max() {
        return this.x_min + this.w_max;
    }

    get x_center() {
        return this.x_min + 0.5 * this.w_max;
    }

    get x_id() {
        return this.x_form + this.w_form;
    }

    get x_form() {
        return this._getX(this.w_form);
    }

    get x_pos() {
        return this._getX(this.w_pos);
    }

    // get x_lemma() {
    //     return this._getX(this.w_lemma);
    // }
    //
    // get x_sem() {
    //     return this._getX(this.w_sem);
    // }

    _getX(width) {
        return this.x_min + 0.5 * (this.w_max - width);
    }
}

class Edge {
    constructor(xd, xh, height, label) {
        this.xd = xd;  // x_dependent
        this.xh = xh;  // x_head
        this.height = height;
        this.label = label;
    }
}

// ======================================== Interface ========================================

function initGraphs() {
    let input = getInputText();

    if (document.getElementById("json_format").checked) {
        D_GRAPHS = initJSON(input);
        INPUT_FORMAT = 'json';
    }
    else if (document.getElementById("tsv_format").checked) {
        D_GRAPHS = initTSV(input);
        INPUT_FORMAT = 'tsv';
    }

    if (D_GRAPHS.length === 0) return;

    // initialize select options
    let ids = getGraphIDs();
    ids.options.length = 0;

    for (let i = 0; i < D_GRAPHS.length; i++) {
        let opt = document.createElement("option");
        opt.value = i.toString();
        opt.text = i.toString();
        ids.appendChild(opt);
    }

    ids.selectedIndex = 0;
    canvasGraph(ids.selectedIndex);
    document.addEventListener("keydown", keyDownHandler, false);
}

function getInputText() {
    return document.getElementById("input_text").value.trim();
}

function getGraphIDs() {
    return document.getElementById("graph_ids");
}

function getGraphContext() {
    return document.getElementById("graph_canvas").getContext("2d");
}

function getZoomRatio() {
    return document.getElementById("zoom_ratio").value;
}

// ======================================== Key Handler ========================================

function keyDownHandler(event) {
    switch (event.keyCode) {
        case 33:
            clickPrevious();
            break;
        case 34:
            clickNext();
            break;
        case 35:
            clickForward();
            break;
        case 36:
            clickBackward();
            break;
    }
}

function clickPrevious() {
    let ids = getGraphIDs();

    if (ids.options.length > 0 && ids.selectedIndex > 0) {
        ids.selectedIndex--;
        canvasGraph(ids.selectedIndex);
    }
}

function clickNext() {
    let ids = getGraphIDs();

    if (ids.options.length > 0 && ids.selectedIndex + 1 < ids.length) {
        ids.selectedIndex++;
        canvasGraph(ids.selectedIndex);
    }
}

function clickForward() {
    let ids = getGraphIDs();

    if (ids.options.length > 0) {
        ids.selectedIndex = ids.length - 1;
        canvasGraph(ids.selectedIndex);
    }
}

function clickBackward() {
    let ids = getGraphIDs();

    if (ids.options.length > 0) {
        ids.selectedIndex = 0;
        canvasGraph(ids.selectedIndex);
    }
}

// ======================================== Geometry ========================================

function canvasGraph(id) {
    let ctx = getGraphContext();
    let zoom = getZoomRatio();
    let graph = D_GRAPHS[id];

    initGeometries(ctx, graph);
    setCanvas(ctx, graph, zoom);
    ctx.scale(zoom, zoom);
    ctx.fillStyle = "#FFFFFF";
    ctx.fillRect(0, 0, ctx.canvas.width * zoom, ctx.canvas.height * zoom);
    drawGraph(ctx, graph);
    window.scrollTo(0, 0);
}

function initGeometries(ctx, graph) {
    function getGeometriesGroups() {
        let lhs = createNestedEmptyArray(graph.size);
        let rhs = createNestedEmptyArray(graph.size);
        let groups = [];

        for (let i = 1; i < graph.size; i++) {
            let curr = graph.get(i);
            let head = curr.parent;

            if (curr.id < head.id) {
                lhs[head.id].push(curr.id);
                rhs[curr.id].push(head.id);
            }
            else {
                lhs[curr.id].push(head.id);
                rhs[head.id].push(curr.id);
            }
        }

        for (let i = 0; i < graph.size; i++) {
            lhs[i] = lhs[i].sort(descendingOrder);
            rhs[i] = rhs[i].sort(descendingOrder);
            groups.push(lhs[i].concat(rhs[i]));
        }

        return groups;
    }

    function getGeometriesLexica(groups) {
        let m, x, pm = 0, size = graph.size;
        let lexica = [];

        //  m: the extra margin when edge lines take more space than the form
        // pm: the previous extra margin
        for (let i = 0; i < size; i++) {
            let rect = new Lexicon(ctx, graph.get(i));

            m = (groups[i].length - 1) * ARC_GAP_W - rect.w_max;
            m = (m > 0) ? 0.5 * m : 0;
            x = (i > 0) ? lexica[i - 1].x_max + FORM_GAP_W : INIT_X;

            rect.x_min = x + m + pm;
            lexica.push(rect);
            pm = m;
        }

        return lexica;
    }

    function getGeometriesEdges(groups, lexica) {
        function aux(id1, id2) {
            return lexica[id1].x_center - (0.5 * (groups[id1].length - 1) - groups[id1].indexOf(id2)) * ARC_GAP_W;
        }

        let heights = getHeights();
        let edges = [null];
        let max = 0;

        for (let i = 1; i < graph.size; i++) {
            let curr = graph.get(i);
            let head = curr.parent;
            let xd = aux(curr.id, head.id);
            let xh = aux(head.id, curr.id);
            let h = heights[i] * ARC_GAP_H;
            max = Math.max(h, max);
            edges.push(new Edge(xd, xh, h, curr.deprel));
        }

        graph.max_height = max;
        return edges;
    }

    function getHeights() {
        let heights = [];

        for (let i = 0; i < graph.size; i++)
            heights.push(0);

        for (let i = 1; i < graph.size; i++)
            getHeightsAux(heights, i);

        validateHeights(graph, heights);
        return heights;
    }

    function getHeightsAux(heights, id) {
        let curr = graph.get(id);
        let head = curr.parent;
        let st, et;

        if (curr.id < head.id) {
            st = curr.id;
            et = head.id;
        }
        else {
            st = head.id;
            et = curr.id;
        }

        let max = 0;

        for (let i = st; i <= et; i++) {
            if (i === id) continue;
            let head = graph.get(i).parent;
            if (head == null) continue;

            if (st <= head.id && head.id <= et) {
                if (heights[i] === 0) getHeightsAux(heights, i);
                max = Math.max(max, heights[i]);
            }
        }

        heights[id] = max + 1;
    }

    function validateHeights(graph, heights) {
        function distance(i) {
            return Math.abs(i - graph.get(i).parent.id);
        }

        let tmp = createNestedEmptyArray(Math.max(...heights) + 1);

        for (let i = 1; i < graph.size; i++)
            tmp[heights[i]].push(i);

        for (let i = 0; i < tmp.length; i++) {
            let counts = Array.from(Array(graph.size), () => 0);
            let group = tmp[i];

            for (let j = 0; j < group.length; j++) {
                let n1 = group[j];
                let h1 = graph.get(n1).parent.id;
                let min1 = Math.min(n1, h1);
                let max1 = Math.max(n1, h1);
                let avg1 = (min1 + max1) / 2;

                for (let k = j + 1; k < group.length; k++) {
                    let n2 = group[k];
                    let h2 = graph.get(n2).parent.id;
                    let min2 = Math.min(n2, h2);
                    let max2 = Math.max(n2, h2);
                    let avg2 = (min2 + max2) / 2;

                    if (min1 < min2 && min2 < max1 && max1 < max2 ||
                        min2 < min1 && min1 < max2 && max2 < max1 ||
                        min1 < avg2 && avg2 < max1 ||
                        min2 < avg1 && avg1 < max2) {
                        counts[n1]++;
                        counts[n2]++;
                    }
                }
            }

            let idx = 1;

            for (let j = 2; j < counts.length; j++) {
                let tm = counts[idx];
                let tj = counts[j];

                if (tm < tj)
                    idx = j;
                else if (tm === tj && distance(idx) < distance(j))
                    idx = j;
            }

            // let idx = counts.reduce((imax, x, i, arr) => x > arr[imax] ? i : imax, 0);
            if (counts[idx] > 0) {
                heights[idx]++;
                group.splice(group.indexOf(idx), 1);
                if (i + 1 < tmp.length) tmp[i + 1].push(idx);
                else tmp.push([idx]);
                i--;
            }
        }
    }

    function getSecondaryGeometriesGroups() {
        let lhs = createNestedEmptyArray(graph.size);
        let rhs = createNestedEmptyArray(graph.size);
        let groups = [];

        for (let i = 1; i < graph.size; i++) {
            let curr = graph.get(i);

            curr.s_heads.forEach(function (arc) {
                let head = arc.node;

                if (curr.id < head.id) {
                    lhs[head.id].push(curr.id);
                    rhs[curr.id].push(head.id);
                }
                else {
                    lhs[curr.id].push(head.id);
                    rhs[head.id].push(curr.id);
                }
            });
        }

        for (let i = 0; i < graph.size; i++) {
            lhs[i] = lhs[i].sort(descendingOrder);
            rhs[i] = rhs[i].sort(descendingOrder);
            groups.push(lhs[i].concat(rhs[i]));
        }

        return groups;
    }

    function getSecondaryGeometriesEdges(groups, lexica) {
        function aux(id1, id2) {
            return lexica[id1].x_center - (0.5 * (groups[id1].length - 1) - groups[id1].indexOf(id2)) * ARC_GAP_W;
        }

        let h, xh, xd;
        let heights = getSecondaryHeights();
        let edges = [null];

        for (let i = 1; i < graph.size; i++) {
            let curr = graph.get(i);
            let es = [];

            for (let j = 0; j < curr.s_heads.length; j++) {
                let arc = curr.s_heads[j];
                let head = arc.node;
                let xd = aux(curr.id, head.id);
                let xh = aux(head.id, curr.id);
                let h = heights[i][j] * ARC_GAP_H;
                es.push(new Edge(xd, xh, h, arc.label));
            }

            edges.push(es);
        }

        return edges;
    }

    function getSecondaryHeights() {
        let heights = [];

        for (let i = 0; i < graph.size; i++)
            heights.push(Array.from(Array(graph.get(i).s_heads.length), () => 0));

        for (let i = 1; i < graph.size; i++)
            getSecondaryHeightsAux(heights, i);

        validateSecondaryHeights(graph, heights);
        return heights;
    }

    function getSecondaryHeightsAux(heights, id) {
        let st, et;
        let curr = graph.get(id);

        for (let i = 0; i < curr.s_heads.length; i++) {
            let head = curr.s_heads[i].node;
            let max = 0;

            if (curr.id < head.id) {
                st = curr.id;
                et = head.id;
            }
            else {
                st = head.id;
                et = curr.id;
            }

            for (let j = st; j <= et; j++) {
                if (j === id) continue;
                let s_heads = graph.get(j).s_heads;

                for (let k = 0; k < s_heads.length; k++) {
                    let head = s_heads[k].node;

                    if (st <= head.id && head.id <= et) {
                        if (heights[j][k] === 0) getSecondaryHeightsAux(heights, j);
                        max = Math.max(max, heights[j][k]);
                    }
                }
            }

            heights[id][i] = max + 1;
        }
    }

    function validateSecondaryHeights(graph, heights) {
        function distance(s) {
            return Math.abs(s[0] - graph.get(s[0]).s_heads[s[1]].node.id);
        }

        let m = Math.max(...heights.map(function (x) {
            return Math.max(...x)
        }));
        if (m === -Infinity) return;
        let tmp = createNestedEmptyArray(m + 1);

        for (let i = 1; i < graph.size; i++) {
            let hs = heights[i];

            for (let j = 0; j < hs.length; j++)
                tmp[hs[j]].push([i, j]);
        }

        for (let i = 0; i < tmp.length; i++) {
            let counts = new Map();
            let group = tmp[i];

            for (let j = 0; j < group.length; j++) {
                let t1 = group[j];
                let n1 = t1[0];
                let h1 = graph.get(n1).s_heads[t1[1]].node.id;
                let min1 = Math.min(n1, h1);
                let max1 = Math.max(n1, h1);
                let avg1 = (min1 + max1) / 2;

                for (let k = j + 1; k < group.length; k++) {
                    let t2 = group[k];
                    let n2 = t2[0];
                    let h2 = graph.get(n2).s_heads[t2[1]].node.id;
                    let min2 = Math.min(n2, h2);
                    let max2 = Math.max(n2, h2);
                    let avg2 = (min2 + max2) / 2;

                    if (min1 < min2 && min2 < max1 && max1 < max2 ||
                        min2 < min1 && min1 < max2 && max2 < max1 ||
                        min1 < avg2 && avg2 < max1 ||
                        min2 < avg1 && avg1 < max2) {
                        counts.set(t1, counts.has(t1) ? counts.get(t1) + 1 : 1);
                        counts.set(t2, counts.has(t2) ? counts.get(t2) + 1 : 1);
                    }
                }
            }

            counts = Array.from(counts.entries(), t => t);
            if (counts.length === 0) continue;
            let idx = 0;

            for (let j = 1; j < counts.length; j++) {
                let tm = counts[idx];
                let tj = counts[j];

                if (tm[1] < tj[1])
                    idx = j;
                else if (tm[1] === tj[1] && distance(tm[0]) < distance(tj[0]))
                    idx = j;
            }

            // let idx = counts.reduce((imax, x, i, arr) => x[1] > arr[imax][1] ? i : imax, 0);
            let t = counts[idx][0];
            heights[t[0]][t[1]]++;

            group.splice(group.indexOf(t), 1);
            if (i + 1 < tmp.length) tmp[i + 1].push(t);
            else tmp.push([t]);
            i--;
        }
    }

    let groups = getGeometriesGroups();
    graph.r_lexica = getGeometriesLexica(groups);
    graph.r_edges = getGeometriesEdges(groups, graph.r_lexica);

    groups = getSecondaryGeometriesGroups();
    graph.r_s_edges = getSecondaryGeometriesEdges(groups, graph.r_lexica);
}

function setCanvas(ctx, graph, zoom) {
    ctx.canvas.width = graph.r_lexica[graph.size - 1].x_max + INIT_X + 25;
    ctx.canvas.width *= zoom;
}

// ======================================== Drawing ========================================

function drawGraph(ctx, graph) {
    function getTextColor(node) {
        if (node.ner.length < 2)
            return "#000000";

        switch (node.ner.slice(2)) {
            case "PERSON"      :
                return "#1E90FF";
            case "NORP"        :
                return "#FFA500";
            case "FAC"         :
                return "#A52A2A";
            case "ORG"         :
                return "#CD853F";
            case "GPE"         :
                return "#2E8B57";
            case "LOC"         :
                return "#9ACD32";
            case "PRODUCT"     :
                return "#FF7F50";
            case "EVENT"       :
                return "#6495ED";
            case "WORK_OF_ART" :
                return "#40E0D0";
            case "LAW"         :
                return "#B0C4DE";
            case "LANGUAGE"    :
                return "#B8860B";
            case "DATE"        :
                return "#F08080";
            case "TIME"        :
                return "#BDB76B";
            case "PERCENT"     :
                return "#8FBC8F";
            case "MONEY"       :
                return "#FF00FF";
            case "QUANTITY"    :
                return "#FF1493";
            case "ORDINAL"     :
                return "#8A2BE2";
            case "CARDINAL"    :
                return "#9370DB";
        }

        return "#000000";
    }

    function drawLexica(y) {
        ctx.beginPath();
        ctx.font = FONT_ID;

        for (let i = 0; i < graph.size; i++) {
            let node = graph.get(i);
            let rect = graph.r_lexica[i];
            ctx.fillStyle = getTextColor(node);

            let id = INPUT_FORMAT === 'tsv' ? node.id : node.id - 1;
            if (id < 0) id = "";
            ctx.fillText(id, rect.x_id + ID_GAP_W, y + ID_GAP_H);
        }

        ctx.closePath();

        ctx.beginPath();
        ctx.font = FONT_FORM;

        for (let i = 0; i < graph.size; i++) {
            let node = graph.get(i);
            let rect = graph.r_lexica[i];
            ctx.fillStyle = getTextColor(node);
            ctx.fillText(node.form, rect.x_form, y);
        }

        ctx.closePath();

        ctx.beginPath();
        ctx.font = FONT_POS;
        ctx.fillStyle = COLOR_POS;
        y += LEXICON_GAP_H;

        for (let i = 0; i < graph.size; i++) {
            let node = graph.get(i);
            let rect = graph.r_lexica[i];
            ctx.fillText(node.pos, rect.x_pos, y);
        }

        ctx.closePath();

        // ctx.beginPath();
        // ctx.font = FONT_SEM;
        // ctx.fillStyle = COLOR_SEM;
        // y += LEXICON_GAP_H;
        //
        // for (let i = 0; i < graph.size; i++) {
        //     let node = graph.get(i);
        //     let rect = graph.r_lexica[i];
        //     ctx.fillText(node.semtag, rect.x_sem, y);
        // }
        //
        // ctx.closePath();
    }

    function drawArcs(y) {
        for (let i = 1; i < graph.size; i++) {
            let edge = graph.r_edges[i];
            let lex = graph.r_lexica[i];
            let yh = y - edge.height;

            drawEdges(edge.xh, edge.xd, y, yh);
            drawDeprel(edge.xh, edge.xd, lex.w_deprel, yh, edge.label);
        }
    }

    function drawEdges(x1, x2, y, yh) {
        // edge
        ctx.beginPath();
        ctx.fillStyle = COLOR_EDGE;
        ctx.strokeStyle = COLOR_EDGE;

        let xl, xr;

        if (x1 < x2) {
            xl = x1;
            xr = x2;
        }
        else {
            xl = x2;
            xr = x1;
        }

        ctx.moveTo(xl, y - 1);
        ctx.lineTo(xl, yh + ARC_RADIUS);
        ctx.arc(xl + ARC_RADIUS, yh + ARC_RADIUS, ARC_RADIUS, Math.PI, 1.5 * Math.PI, false);
        ctx.lineTo(xr - ARC_RADIUS, yh);
        ctx.arc(xr - ARC_RADIUS, yh + ARC_RADIUS, ARC_RADIUS, 1.5 * Math.PI, 0, false);
        ctx.lineTo(xr, y - 1);
        ctx.stroke();

        ctx.closePath();

        // anchor
        ctx.beginPath();

        ctx.moveTo(x2, y);
        ctx.lineTo(x2 - ANCHOR_W, y - ANCHOR_H);
        ctx.lineTo(x2 + ANCHOR_W, y - ANCHOR_H);
        ctx.fill();

        ctx.closePath();
    }

    function drawDeprel(x1, x2, w, yh, deprel) {
        ctx.beginPath();
        ctx.font = FONT_DEPREL;

        let x = x2 - 0.5 * (x2 - x1 + w);
        let y = yh + DEPREL_SHIFT_H;

        ctx.fillStyle = "#FFFFFF";    // background: white
        ctx.fillRect(x - DEPREL_SHIFT_W, yh - DEPREL_SHIFT_H, w + 2 * DEPREL_SHIFT_W, 2 * DEPREL_SHIFT_H);

        // label foreground
        ctx.fillStyle = COLOR_DEPREL;
        ctx.fillText(deprel, x, y);

        ctx.closePath();
    }

    function drawSecondaryArcs(y) {
        for (let i = 1; i < graph.size; i++) {
            let edges = graph.r_s_edges[i];
            let lex = graph.r_lexica[i];

            for (let j = 0; j < edges.length; j++) {
                let edge = edges[j];
                let yh = y + edge.height;
                drawSecondaryEdges(edge.xh, edge.xd, y, yh);
                drawDeprel(edge.xh, edge.xd, lex.w_s_deprels[j], yh, edge.label);
            }
        }
    }

    function drawSecondaryEdges(x1, x2, y, yh) {
        // edge
        ctx.beginPath();
        ctx.fillStyle = COLOR_EDGE;

        let xl, xr;

        if (x1 < x2) {
            xl = x1;
            xr = x2;
        }
        else {
            xl = x2;
            xr = x1;
        }

        ctx.moveTo(xl, y + 1);
        ctx.lineTo(xl, yh - ARC_RADIUS);
        ctx.arc(xl + ARC_RADIUS, yh - ARC_RADIUS, ARC_RADIUS, Math.PI, 0.5 * Math.PI, true);
        ctx.lineTo(xr - ARC_RADIUS, yh);
        ctx.arc(xr - ARC_RADIUS, yh - ARC_RADIUS, ARC_RADIUS, 0.5 * Math.PI, 0, true);
        ctx.lineTo(xr, y + 1);
        ctx.stroke();

        ctx.closePath();

        // anchor
        ctx.beginPath();

        ctx.moveTo(x2, y);
        ctx.lineTo(x2 - ANCHOR_W, y + ANCHOR_H);
        ctx.lineTo(x2 + ANCHOR_W, y + ANCHOR_H);
        ctx.fill();

        ctx.closePath();
    }

    let y = graph.max_height + FORM_GAP_H + INIT_Y;
    drawLexica(y);
    drawArcs(y - FORM_GAP_H);
    drawSecondaryArcs(y + LEXICON_GAP_H + 5);
}

// ============================== Helpers ==============================

function readFile(f) {
    let reader = new FileReader();
    reader.readAsText(f);

    reader.onload = function () {
        document.getElementById("input_text").value = reader.result;
        initGraphs();
    };
}

function exportCanvas() {
    let ctx = document.getElementById("graph_canvas");
    let img = ctx.toDataURL("image/png");
    window.open(img);
}

function selectGraph() {
    let ids = getGraphIDs();
    canvasGraph(ids.selectedIndex);
}

function createNestedEmptyArray(length) {
    let array = [];

    for (let i = 0; i < length; i++)
        array.push([]);

    return array;
}

function descendingOrder(a, b) {
    return b - a;
}
