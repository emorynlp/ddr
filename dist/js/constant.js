const HEAD = '\
<title>Emory Dependencies</title>\
<link href="../dist/css/bootstrap.min.css" rel="stylesheet">\
<link href="../dist/css/sb-admin-2.css" rel="stylesheet">\
<link href="../dist/css/font-awesome.min.css" rel="stylesheet" type="text/css">\
<script src="../dist/js/jquery.min.js"></script>\
<script src="../dist/js/bootstrap.min.js"></script>\
<script src="../dist/js/metisMenu.min.js"></script>\
<script src="../dist/js/sb-admin-2.js"></script>';

const NAV = '\
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">\
    <div class="navbar-header">\
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">\
            <span class="sr-only">Toggle navigation</span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
            <span class="icon-bar"></span>\
        </button>\
        <a class="navbar-brand" href="index.html">Emory Dependencies</a>\
    </div>\
    <div class="navbar-default sidebar" role="navigation">\
        <div class="sidebar-nav navbar-collapse">\
            <ul class="nav" id="side-menu">\
                <li>\
                    <a href="index.html">Overview</a>\
                </li>\
                <li>\
                    <a href="#">Primary Dependencies<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">\
                        <li><a href="ccomp.html">ccomp: clausal complement</a></li>\
                        <li><a href="dobj.html">dobj: direct object</a></li>\
                        <li><a href="relcl.html">relcl: relative clause</a></li>\
                        <li><a href="sprd.html">sprd: small clausal predicate</a></li>\
                        <li><a href="xcomp.html">xcomp: open clausal complement</a></li>\
                    </ul>\
                </li>\
                <li>\
                    <a href="#">Secondary Dependencies<span class="fa arrow"></span></a>\
                    <ul class="nav nav-second-level">\
                        <li><a href="ssubj.html">ssubj: small clausal subject</a></li>\
                        <li><a href="xsubj.html">xsubj: open clausal subject</a></li>\
                    </ul>\
                </li>\
            </ul>\
        </div>\
    </div>\
</nav>';


const FOOTER = '<footer class="site-footer"><span class="site-footer-credits">&copy;'+new Date().getFullYear()+' by <a href="http://nlp.mathcs.emory.edu">Emory NLP</a>. All Rights Reserved.</span></footer>'

