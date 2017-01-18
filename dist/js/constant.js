const HEAD = '\
<title>Deep Dependency Representation</title>\
<link href="../dist/css/bootstrap.min.css" rel="stylesheet">\
<link href="../dist/css/sb-admin-2.css" rel="stylesheet">\
<link href="../dist/css/font-awesome.min.css" rel="stylesheet" type="text/css">\
<script src="../dist/js/jquery.min.js"></script>\
<script src="../dist/js/bootstrap.min.js"></script>\
<script src="../dist/js/metisMenu.min.js"></script>\
<script src="../dist/js/sb-admin-2.js"></script>';

const DEPENDENCIES = '\
<li><a href="overview.html">Overview</a></li>\
<li><a href="acl.html"><code>acl</code>: clausal modifier of noun</a></li>\
<li><a href="adv.html"><code>adv</code>: adverbial</a></li>\
<li><a href="advcl.html"><code>advcl</code>: adverbial clause</a></li>\
<li><a href="advnp.html"><code>advnp</code>: adverbial noun phrase</a></li>\
<li><a href="appo.html"><code>appo</code>: apposition</a></li>\
<li><a href="attr.html"><code>attr</code>: attribute</a></li>\
<li><a href="aux.html"><code>aux</code>: auxiliary verb</a></li>\
<li><a href="case.html"><code>case</code>: case marker</a></li>\
<li><a href="cc.html"><code>cc</code>: coordinating conjunction</a></li>\
<li><a href="com.html"><code>com</code>: compound word</a></li>\
<li><a href="comp.html"><code>comp</code>: complement</a></li>\
<li><a href="conj.html"><code>conj</code>: conjunct</a></li>\
<li><a href="cop.html"><code>cop</code>: copula</a></li>\
<li><a href="csbj.html"><code>csbj</code>: clausal subject</a></li>\
<li><a href="dat.html"><code>dat</code>: dative</a></li>\
<li><a href="dep.html"><code>dep</code>: unclassified dependency</a></li>\
<li><a href="det.html"><code>det</code>: determiner</a></li>\
<li><a href="disc.html"><code>disc</code>: discourse element</a></li>\
<li><a href="expl.html"><code>expl</code>: expletive</a></li>\
<li><a href="lv.html"><code>lv</code>: light verb</a></li>\
<li><a href="mark.html"><code>mark</code>: clausal marker</a></li>\
<li><a href="meta.html"><code>meta</code>: meta element</a></li>\
<li><a href="modal.html"><code>modal</code>: modal (verb or adjective)</a></li>\
<li><a href="neg.html"><code>neg</code>: negation</a></li>\
<li><a href="nsbj.html"><code>nsbj</code>: nominal subject</a></li>\
<li><a href="num.html"><code>num</code>: numeric modifier</a></li>\
<li><a href="obj.html"><code>obj</code>: (direct or preposition) object</a></li>\
<li><a href="p.html"><code>p</code>: punctuation or symbol</a></li>\
<li><a href="poss.html"><code>poss</code>: possessive modifier</a></li>\
<li><a href="ppmod.html"><code>ppmod</code>: prepositional phrase</a></li>\
<li><a href="prn.html"><code>prn</code>: parenthetical notation</a></li>\
<li><a href="prt.html"><code>prt</code>: verb particle</a></li>\
<li><a href="raise.html"><code>raise</code>: raising verb</a></li>\
<li><a href="r-*.html"><code>r-*</code>: referential</a></li>\
<li><a href="relcl.html"><code>relcl</code>: relative clause</a></li>\
<li><a href="root.html"><code>root</code>: root</a></li>\
<li><a href="voc.html"><code>voc</code>: vocative</a></li>';

const SEMANTIC_ROLES = '\
<li><a href="bnf.html"><code>bnf</code>: benefactive</a></li>\
<li><a href="dir.html"><code>dir</code>: direction</a></li>\
<li><a href="ext.html"><code>ext</code>: extent</a></li>\
<li><a href="loc.html"><code>loc</code>: locative</a></li>\
<li><a href="mnr.html"><code>mnr</code>: manner</a></li>\
<li><a href="prp.html"><code>prp</code>: purpose</a></li>\
<li><a href="tmp.html"><code>tmp</code>: temporal</a></li><br>';

const NAV = '\
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">\
    <div class="navbar-header">\
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">\
            <span class="sr-only">Toggle navigation</span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
        </button>\
        <a class="navbar-brand" href="overview.html">Deep Dependency Representation</a>\
    </div>\
    <ul class="nav navbar-top-links navbar-right">\
        <li><a href="https://github.com/emorynlp/ddr">DDR Home</a></li>\
        <li><a href="http://nlp.mathcs.emory.edu">Emory NLP</a></li>\
    </ul>\
    <div class="navbar-default sidebar" role="navigation">\
        <div class="sidebar-nav navbar-collapse">\
            <ul class="nav" id="side-menu">\
                <li>\
                    <a href="#.html">Dependencies<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">'+DEPENDENCIES+'</ul>\
                </li>\
                <li>\
                    <a href="#">Semantic Roles<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">'+SEMANTIC_ROLES+'</ul>\
                </li>\
            </ul>\
        </div>\
    </div>\
</nav>';


const FOOTER = '<footer class="site-footer"><span class="site-footer-credits">&copy;2016 - '+new Date().getFullYear()+' by <a href="http://nlp.mathcs.emory.edu">Emory NLP</a>. All Rights Reserved.</span></footer>'

