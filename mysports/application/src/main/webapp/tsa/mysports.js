/* 
 *  Copyright 2012, Oracle and or its affiliates. All Rights Reserved.
 *  
 *  @author irfan.ahmed@oracle.com
 */

dojo.provide("demo.demo");

dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.data.ObjectStore");
dojo.require("dijit._Widget");
dojo.require("dijit.Tree");
dojo.require("dijit.tree.ForestStoreModel");

/**
 * MySports Server connectivity
 */
dojo.declare("mysports.Server", null, {

	rootURL : "/MySports/persistence/mysports-",

	league : 'HTHL',

	constructor : function(args) {
		rootURL = args.root;
		league = args.league;
	},

	get : function(args) {
		if (!(args.url && args.onSuccess)) {
			throw "url/onSuccess parameters are required for XHR Get call.";
		}
		dojo.xhrGet({
			url : args.url,
			headers : {
				"Accept" : "application/json",
				"Content-Type" : "application/json"
			},
			handleAs : "json",
			load : function(data, ioArgs) {
				args.onSuccess.apply(args.context, [ data, ioArgs ]);
			},
			error : function(error, ioArgs) {
				console.error("Error occurred: ", error);
			},
			timeout : 15000,
			sync : (args.sync ? args.sync : false)
		});
	}
});

dojo.declare("mysports.NavPanel", [ dijit._Widget ], {
	constructor : function() {
		this.treeData = {
			label : "name",
			identifier : "id",
			items : []
		};
	},

	init : function() {
		var searchParams = dojo.queryToObject(dojo.doc.location.search
				.substr((dojo.doc.location.search[0] === "?" ? 1 : 0)));
		console.debug("Search Params: ", searchParams);
		mysports.server.league = searchParams.league;
		console.debug("Set leaue to: ", searchParams.league);

		if (!mysports.server.league) {
			mysports.server.league = 'OSL';
		}

		dojo.byId("leagueLogo").src = "/MySportsAdmin/rest/league/"
				+ mysports.server.league + ".png";

		mysports.server.get({
			url : "/MySports/persistence/mysports-" + mysports.server.league
					+ "/query/Division.findAll",
			onSuccess : function(divisions) {
				this.divisions = divisions;
				var divLength = this.divisions.length;
				dojo.forEach(this.divisions, function(division, divIndex) {
					division.type = "division";
					if (!division.children) {
						division.children = [];
					}
					this.getTeam(division, function() {
						console
								.debug("Loaded Teams for division ",
										division.id);
						this.treeData.items.push(division);
						if (divIndex == (divLength - 1)) {
							this.createTree();
						}
					});
				}, this);
			},
			context : this
		});
	},

	getTeam : function(division, doneLoadingTeams) {
		var teams = [];
		dojo.forEach(division.relationships, function(rel) {
			if (!rel.id) {
				rel.id = division.id + "_" + rel.rel;
			}
			if (rel.rel == "teams") {
				teams.push(rel);
			}
		});
		var teamLength = teams.length;
		dojo.forEach(teams, function(teamInfo, index) {
			mysports.server.get({
				url : teamInfo.href,
				onSuccess : function(teamData) {
					console.debug("Team Data for divsion : ", division.id,
							teamData);

					dojo.forEach(teamData, function(team) {
						team.type = "team";
						dojo.forEach(team.relationships, function(teamRel) {
							if (!teamRel.id) {
								teamRel.id = team.id + "_" + teamRel.rel;
							}
						});
						division.children.push(team);
					});
					if (index == (teamLength - 1)) {
						if (doneLoadingTeams) {
							doneLoadingTeams.call(this);
						}
					}
				},
				context : this
			});
		}, this);
	},

	createTree : function() {
		console.debug("Creating Tree : ", dojo.toJson(this.treeData, "  "));
		this.treeStore = new dojo.data.ItemFileReadStore({
			data : this.treeData
		});
		this.treeStore.fetch({
			query : {
				type : "division"
			},
			onItem : function(item) {
				console.debug(item);
			},
			onError : function(error) {
				console.error(error);
			}
		});
		this.treeModel = new dijit.tree.ForestStoreModel({
			store : this.treeStore,
			query : {
				type : "division"
			},
			rootId : "root",
			rootLabel : "Divisions",
			childrenAttrs : [ "children" ]
		});
		this.tree = new dijit.Tree({
			model : this.treeModel
		}, "tree");
		this.connect(this.tree, "onClick", function(item) {
			if (this.treeStore.isItem(item)) {
				var type = this.treeStore.getValue(item, "type");
				var name = this.treeStore.getValue(item, "name");
				if (type == "team") {
					var relationShips = this.treeStore.getValues(item,
							"relationships");
					var players = dojo
							.filter(relationShips,
									function(rel) {
										return this.treeStore.getValue(rel,
												"rel") == "players";
									}, this)[0];
					this.showPlayers(name, players);
				} else if (type == "division") {
					this.showDivision(item);
				}
			}
		});
		this.createPlayersTable();
	},

	showPlayers : function(teamName, item) {
		var href = this.treeStore.getValue(item, "href");
		console.debug("Showing Players : ", href);
		mysports.server.get({
			url : href,
			onSuccess : function(playersInfo) {
				console.debug("Players : ", playersInfo);
				this.playersTable.destroyAllRows();
				this.playersTable.setCaption("Team: " + teamName);
				dojo.forEach(playersInfo, function(player) {
					this.playersTable.addRow(player);
				}, this);
			},
			context : this
		});
	},

	showDivision : function(item) {
		console.debug("Showing Division : ", item);
	},

	createPlayersTable : function() {
		this.creatingTable = true;
		mysports.server
				.get({
					url : "/MySports/persistence/mysports-"
							+ mysports.server.league
							+ "/metadata/entity/Player",
					onSuccess : function(tableInfo) {
						this.playerTableInfo = tableInfo;
						this.playersTable = new demo.PlayersTable(
								this.playerTableInfo);
						dojo.byId("playersTableDiv").appendChild(
								this.playersTable.domNode);
						this.creatingTable = false;
					},
					context : this
				});
	}
});

dojo.declare("demo.PlayersTable", [ dijit._Widget ], {
	constructor : function(args) {
		this.info = args;
		this.cols = [];
		this.rows = [];
	},

	postCreate : function() {
		this.domNode = dojo.create("table", {
			"class" : "playersTable",
			border : "1",
			rules : "all"
		});
		this.captionNode = dojo.create("caption", null, this.domNode);
		this.tableNode = dojo.create("tbody", null, this.domNode);
		this.createCols();
	},

	createCols : function() {
		var headerRow = dojo.create("tr", null, this.tableNode);
		dojo.forEach(this.attributes, function(colInfo, index) {
			// TODO: Filter out some columns
			if (colInfo.name != "team" && colInfo.name != "version"
					&& colInfo.name != "userid") {
				this.cols.push(colInfo.name);
				dojo.create("th", {
					"class" : (index == 0 ? "first"
							: (index == this.attributes.length - 1 ? "last"
									: "")),
					innerHTML : colInfo.name
				}, headerRow);
			}
		}, this);
	},

	setCaption : function(caption) {
		dojo.attr(this.captionNode, "innerHTML", caption);
	},

	addRow : function(rowInfo) {
		console.debug("Adding : ", rowInfo);
		var row = dojo.create("tr", null, this.tableNode);
		dojo.forEach(this.cols, function(name) {
			dojo.create("td", {
				innerHTML : (rowInfo[name] ? rowInfo[name] : "")
			}, row);
		});
		this.rows.push(row);
	},

	destroyAllRows : function() {
		dojo.forEach(this.rows, function(row) {
			dojo.destroy(row);
		});
	}
});
