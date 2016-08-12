const HEAD = '\
<title>Deep Semantic Representations</title>\
<link href="../dist/css/bootstrap.min.css" rel="stylesheet">\
<link href="../dist/css/sb-admin-2.css" rel="stylesheet">\
<link href="../dist/css/font-awesome.min.css" rel="stylesheet" type="text/css">\
<script src="../dist/js/jquery.min.js"></script>\
<script src="../dist/js/bootstrap.min.js"></script>\
<script src="../dist/js/metisMenu.min.js"></script>\
<script src="../dist/js/sb-admin-2.js"></script>';

const DEPENDENCY_RELATIONS = '\
<li><a href="acl.html"><code>acl</code>: clausal modifier of noun</a></li>\
<li><a href="advcl.html"><code>advcl</code>: adverbial clause</a></li>\
<li><a href="advmod.html"><code>advmod</code>: adverbial modifier</a></li>\
<li><a href="appos.html"><code>appos</code>: apposition</a></li>\
<li><a href="aux.html"><code>aux</code>: auxiliary</a></li>\
<li><a href="case.html"><code>case</code>: case marker</a></li>\
<li><a href="cc.html"><code>cc</code>: coordinating conjunction</a></li>\
<li><a href="ccomp.html"><code>ccomp</code>: clausal complement</a></li>\
<li><a href="compound.html"><code>compound</code>: compound word</a></li>\
<li><a href="conj.html"><code>conj</code>: conjunct</a></li>\
<li><a href="csubj.html"><code>csubj</code>: clausal subject</a></li>\
<li><a href="dative.html"><code>dative</code>: dative</a></li>\
<li><a href="dep.html"><code>dep</code>: unclassified dependency</a></li>\
<li><a href="det.html"><code>det</code>: determiner</a></li>\
<li><a href="discourse.html"><code>discourse</code>: discourse element</a></li>\
<li><a href="dobj.html"><code>dobj</code>: direct object</a></li>\
<li><a href="expl.html"><code>expl</code>: expletive</a></li>\
<li><a href="lobj.html"><code>lobj</code>: logical object</a></li>\
<li><a href="lsubj.html"><code>lsubj</code>: logical subject</a></li>\
<li><a href="meta.html"><code>meta</code>: meta element</a></li>\
<li><a href="neg.html"><code>neg</code>: negation</a></li>\
<li><a href="nmod.html"><code>nmod</code>: nominal modifier</a></li>\
<li><a href="npmod.html"><code>npmod</code>: adverbial noun phrase</a></li>\
<li><a href="nsubj.html"><code>nsubj</code>: nominal subject</a></li>\
<li><a href="nummod.html"><code>nummod</code>: numeric modifier</a></li>\
<li><a href="parataxis.html"><code>parataxis</code>: parataxis</a></li>\
<li><a href="pcomp.html"><code>pcomp</code>: prepositional complement</a></li>\
<li><a href="poss.html"><code>poss</code>: possessive modifier</a></li>\
<li><a href="prt.html"><code>prt</code>: verb particle</a></li>\
<li><a href="punct.html"><code>punct</code>: punctuation</a></li>\
<li><a href="root.html"><code>root</code>: root</a></li>\
<li><a href="scomp.html"><code>scomp</code>: small clausal complement</a></li>\
<li><a href="topic.html"><code>topic</code>: topic</a></li>\
<li><a href="vocative.html"><code>vocative</code>: vocative</a></li>\
<li><a href="xcomp.html"><code>xcomp</code>: open clausal complement</a></li>';

const SECONDARY_DEPENDENCIES = '\
<li><a href="conj.html">Coordination</li>\
<li><a href="scomp.html">Small clause</li>';

const SEMANTIC_ROLES = '\
<li><a href="bnf.html"><code>bnf</code>: benefactive</a></li>\
<li><a href="dir.html"><code>dir</code>: direction</a></li>\
<li><a href="ext.html"><code>ext</code>: extent</a></li>\
<li><a href="loc.html"><code>loc</code>: locative</a></li>\
<li><a href="mnr.html"><code>mnr</code>: manner</a></li>\
<li><a href="prp.html"><code>prp</code>: purpose</a></li>\
<li><a href="tmp.html"><code>tmp</code>: temporal</a></li>';

const NAV = '\
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">\
    <div class="navbar-header">\
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">\
            <span class="sr-only">Toggle navigation</span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
        </button>\
        <a class="navbar-brand" href="overview.html">Deep Semantic Representations (DSR)</a>\
    </div>\
    <div class="navbar-default sidebar" role="navigation">\
        <div class="sidebar-nav navbar-collapse">\
            <ul class="nav" id="side-menu">\
                <li><a href="overview.html">Overview</a></li>\
                <li>\
                    <a href="#">Dependency Relations<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">'+DEPENDENCY_RELATIONS+'</ul>\
                </li>\
                <li>\
                    <a href="#">Semantic Roles<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">'+SEMANTIC_ROLES+'</ul>\
                </li>\
            </ul>\
        </div>\
    </div>\
</nav>';


const FOOTER = '<footer class="site-footer"><span class="site-footer-credits">&copy;'+new Date().getFullYear()+' by <a href="http://nlp.mathcs.emory.edu">Emory NLP</a>. All Rights Reserved.</span></footer>'

