<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: Arte
  Date: 07.12.2014
  Time: 18:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <title>jQuery UI Tabs - Default functionality</title>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css">
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
    <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="jquery.tooltipster.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/spin.js/1.2.7/spin.min.js"></script>
    <link rel="stylesheet" type="text/css" href="tooltipster.css"/>

    <script>
        $(document).ready(function () {
        	var opts = {
                    lines: 10, // The number of lines to draw
                    length: 7, // The length of each line
                    width: 4, // The line thickness
                    radius: 15, // The radius of the inner circle
                    corners: 1, // Corner roundness (0..1)
                    rotate: 0, // The rotation offset
                    color: '#000', // #rgb or #rrggbb
                    speed: 1, // Rounds per second
                    trail: 60, // Afterglow percentage
                    shadow: false, // Whether to render a shadow
                    hwaccel: false, // Whether to use hardware acceleration
                    className: 'spinner', // The CSS class to assign to the spinner
                    zIndex: 2e9, // The z-index (defaults to 2000000000)
                    top: 25, // Top position relative to parent in px
                    left: 25 // Left position relative to parent in px
                };
                var target = document.getElementById('foo');
            $('.tooltip').tooltipster();
            $('#crawlData').click(function () {
                var spinner = new Spinner(opts).spin(target);
                $.getJSON('CrawlServlet', function(data) {
                    console.log(data);
                    for (var i = 0; i < data.children.length; i++){
                        $('#crawledSites').append(data.children[i].name + "<br>");
                    }
                    fillTree(data);
                    spinner.stop();
                }).fail(function( jqxhr, textStatus, error ) {
                            var err = textStatus + ', ' + error;
                            console.log("Request Failed: " + err);
                        });
            });
        });

        $(function () {
            $("#tabs").tabs();
        });


    </script>

    <style type="text/css">
        .ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
            background-image: none;
            background-color: steelblue;
        }

        body {
            width: 740px;
        }

        .ui-tabs-nav {
            background: transparent;
            border-width: 0px 0px 1px 0px;
            -moz-border-radius: 0px;
            -webkit-border-radius: 0px;
            border-radius: 0px;
        }

        #tabs {
            text-align: center;
        }
        
        #foo {
            width: 100px;
            height: 100px;
            position: absolute;
            top:0;
            bottom: 0;
            left: 0;
            right: 0;
            margin: auto;
        }

        #tabs .ui-state-active a {
            color: #ffffff !important;
        }

        .ui-tabs .ui-tabs-nav li a {
            min-width: 200px;
            white-space: normal;
        }

        .node circle {
            fill: #fff;
            stroke: steelblue;
            stroke-width: 1.5px;
        }

        .node {
            font: 10px sans-serif;
        }

        button {
            float: left;
        }

        .link {
            fill: none;
            stroke: #ccc;
            stroke-width: 1.5px;
        }

        #tabs-1 input {
            float: left;
        }

        button {
            border: solid 2px steelblue;
            border-radius: 3px;
            moz-border-radius: 3px;
            font-size: 20px;
            color: #ffffff;
            padding: 1px 17px;
            background-color: steelblue;
        }

        button:hover {
            background: lightsteelblue;
            background-image: -webkit-linear-gradient(top, #3cb0fd, #3498db);
            background-image: -moz-linear-gradient(top, #3cb0fd, #3498db);
            background-image: -ms-linear-gradient(top, #3cb0fd, #3498db);
            background-image: -o-linear-gradient(top, #3cb0fd, #3498db);
            background-image: linear-gradient(to bottom, #3cb0fd, #3498db);
            text-decoration: none;
        }
        
        div.tooltip {   
		  position: absolute;           
		  text-align: center;           
		  width: 300px;                  
		  height: 50px;                 
		  padding: 2px;             
		  font: 12px sans-serif;        
		  background: lightsteelblue;   
		  border: 0px;      
		  border-radius: 8px;           
		  pointer-events: none;         
		}

    </style>

</head>
<body>

<div id="tabs">
    <ul>
        <li><a href="#tabs-1">Crawled websites</a></li>
        <li><a href="#tabs-2">Topic Graph</a></li>
        <li><a href="#tabs-3">Other stuff</a></li>
    </ul>
    <div id="tabs-1">
        <form name="crawlAndQuery">
<!-- 
            <p>
                <input name="query" disabled= "true" placeholder="e.g. value1 value2 value3" size="44"/> Query
            </p>

            <p>
                <input name="site" disabled= "true" placeholder="http://www.example.org" size="44"/> Seite
            </p>
 -->

            <br>

            <p>
                <button type="button" id="crawlData">Drück mich, um zu Crawlen</button>
            </p>
            <p>

            <div id='foo'></div>

            </p>
            <br>
        </form>
        <hr>
        <div id = "crawledSites" title="Crawled Sites:">

        </div>
        <br><br>
    </div>
    <div id="tabs-2">
        Topic-graph
    </div>
    <div id="tabs-3">
        <img src="http://www.seitenfenster.org/wp-content/uploads/2012/09/wpid-A3GraNqCAAA6WYF.jpg">
    </div>
</div>

<script>
    function fillTree(root) {
        var diameter = 750;

        var tree = d3.layout.tree()
                .size([360, diameter / 2 - 120])
                .separation(function (a, b) {
                    return (a.parent == b.parent ? 1 : 2) / a.depth;
                });

        var diagonal = d3.svg.diagonal.radial()
                .projection(function (d) {
                    return [d.y, d.x / 180 * Math.PI];
                });

        var svg = d3.select("#tabs-2").append("svg")
                .attr("width", diameter)
                .attr("height", diameter - 150)
                .append("g")
                .attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");


            var nodes = tree.nodes(root),
                    links = tree.links(nodes);

            var link = svg.selectAll(".link")
                    .data(links)
                    .enter().append("path")
                    .attr("class", "link")
                    .attr("d", diagonal);

            var div = d3.select("body").append("div")   // für Tooltip
            .attr("class", "tooltip")               
            .style("opacity", 0);
            
            var node = svg.selectAll(".node")
                    .data(nodes)
                    .enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function (d) {
                        return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
                    })
             .on("mouseover", function(d) {      
            div.transition()        
                .duration(200)      
                .style("opacity", .9);      
            div .html( d.name + "<br/>")  
                .style("left", (d3.event.pageX) + "px")     
                .style("top", (d3.event.pageY - 28) + "px");    
            })                  
        .on("mouseout", function(d) {       
            div.transition()        
                .duration(500)      
                .style("opacity", 0);   
        });

            node.append("circle")
                    .attr("r", 4.5);

            node.append("text")
                    .attr("dy", ".31em")
                    .attr("text-anchor", function (d) {
                        return d.x < 180 ? "start" : "end";
                    })
                    .attr("transform", function (d) {
                        return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)";
                    })
                    .text(function (d) {
                        return d.name;
                    });


        d3.select(self.frameElement).style("height", diameter - 150 + "px");
    }
</script>

</body>
</html>
