window.__require = (function t(e, n, r) {
  function o(s, c) {
    if (!n[s]) {
      if (!e[s]) {
        var a = s.split("/");
        if (((a = a[a.length - 1]), !e[a])) {
          var u = "function" == typeof __require && __require;
          if (!c && u) return u(a, !0);
          if (i) return i(a, !0);
          throw new Error("Cannot find module '" + s + "'");
        }
        s = a;
      }
      var l = (n[s] = { exports: {} });
      e[s][0].call(
        l.exports,
        function (t) {
          return o(e[s][1][t] || t);
        },
        l,
        l.exports,
        t,
        e,
        n,
        r
      );
    }
    return n[s].exports;
  }
  for (
    var i = "function" == typeof __require && __require, s = 0;
    s < r.length;
    s++
  )
    o(r[s]);
  return o;
})(
  {
    AssetsManager: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "0fb540xLEhMa5oE5wYOOJog", "AssetsManager"),
          Object.defineProperty(n, "__esModule", { value: !0 });
        var r = t("./events/Emitter"),
          o = new ((function () {
            function t() {
              (this.objects = {}),
                (this._resourcesAssets = {}),
                (this.fetchings = {}),
                (this.callbacks = {});
            }
            return (
              (t.prototype.get = function (t, e) {
                var n = this._resourcesAssets[t];
                if (n) return n;
                var r = "res",
                  o = t.indexOf("/");
                o > -1 && (r = t.substring(0, o));
                var i = t.substring(o + 1);
                return cc.assetManager.getBundle(r).get(i, e);
              }),
              (t.prototype.inst = function (t, e) {
                var n = this.get(t, e);
                return n ? cc.instantiate(n) : null;
              }),
              (t.prototype.request = function (t, e, n) {
                var o = this,
                  i = (this.callbacks[t] =
                    this.callbacks[t] || new r.default());
                if (this.fetchings[t]) i.once(e, n);
                else {
                  i.once(e, n);
                  var s = this._resourcesAssets[t];
                  if (s) i.fire([null, s]);
                  else {
                    this.fetchings[t] = !0;
                    var c = new XMLHttpRequest();
                    c.open("GET", t, !0),
                      (c.onload = function () {
                        var e = c.response;
                        try {
                          var n = JSON.parse(e);
                          (o._resourcesAssets[t] = n),
                            (o.fetchings[t] = !1),
                            i.fire([null, n]);
                        } catch (r) {}
                      }),
                      (c.onerror = function () {}),
                      c.send();
                  }
                }
              }),
              (t.prototype.loadRemote = function (t, e, n) {
                var o = this,
                  i = (this.callbacks[t] =
                    this.callbacks[t] || new r.default());
                if (this.fetchings[t]) i.once(e, n);
                else {
                  i.once(e, n);
                  var s = this._resourcesAssets[t];
                  s
                    ? i.fire([null, s])
                    : ((this.fetchings[t] = !0),
                      cc.assetManager.loadRemote(t, function (e, n) {
                        (o._resourcesAssets[t] = n),
                          (o.fetchings[t] = !1),
                          i.fire([e, n]);
                      }));
                }
              }),
              (t.prototype.resources = function (t, e, n) {
                var o = this,
                  i = (this.callbacks[t] =
                    this.callbacks[t] || new r.default());
                if (this.fetchings[t]) i.once(e, n);
                else {
                  i.once(e, n);
                  var s = this._resourcesAssets[t];
                  s
                    ? i.fire([null, s])
                    : ((this.fetchings[t] = !0),
                      cc.resources.load(t, function (e, n) {
                        (o._resourcesAssets[t] = n),
                          (o.fetchings[t] = !1),
                          i.fire([e, n]);
                      }));
                }
              }),
              t
            );
          })())();
        (n.default = o), cc._RF.pop();
      },
      { "./events/Emitter": "Emitter" },
    ],
    Delegate: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "d7af2gYjfFLq54WwBwEUwN+", "Delegate");
        var r =
          (this && this.__spreadArrays) ||
          function () {
            for (var t = 0, e = 0, n = arguments.length; e < n; e++)
              t += arguments[e].length;
            var r = Array(t),
              o = 0;
            for (e = 0; e < n; e++)
              for (var i = arguments[e], s = 0, c = i.length; s < c; s++, o++)
                r[o] = i[s];
            return r;
          };
        Object.defineProperty(n, "__esModule", { value: !0 }),
          (n.Delegate = void 0);
        var o = (function () {
          function t() {
            (this._flag = 0), (this._items = []);
          }
          return (
            (t.prototype.add = function (t, e, n) {
              var r = this._items,
                o = r.findIndex(function (n, r, o) {
                  return n == t && o[r + 1] == e;
                });
              -1 != o ? ((r[o + 2] = n), (r[o + 3] = 1)) : r.push(t, e, n, 1);
            }),
            (t.prototype.once = function (t, e, n) {
              var r = this._items,
                o = r.findIndex(function (n, r, o) {
                  return n == t && o[r + 1] == e;
                });
              -1 != o ? ((r[o + 2] = n), (r[o + 3] = 2)) : r.push(t, e, n, 2);
            }),
            (t.prototype.remove = function (t, e) {
              var n = this._items,
                r = n.findIndex(function (n, r, o) {
                  return n == t && o[r + 1] == e;
                });
              -1 != r &&
                (0 != this._flag
                  ? ((n[r + 3] = 0), (this._flag = 2))
                  : n.splice(r, 4));
            }),
            (t.prototype.clear = function () {
              var t = this._items;
              0 != this._flag
                ? (t.forEach(function (t, e, n) {
                    e % 4 == 3 && (n[e] = 0);
                  }),
                  (this._flag = 2))
                : (t.length = 0);
            }),
            (t.prototype.clearForTarget = function (t) {
              if (t) {
                var e = this._items;
                if (0 != this._flag)
                  e.forEach(function (e, n, r) {
                    n % 4 == 1 && r[n] == t && (r[n + 2] = 0);
                  }),
                    (this._flag = 2);
                else
                  for (var n = e.length - 4; n >= 0; )
                    e[n + 1] == t && e.splice(n, 4), (n -= 4);
              }
            }),
            Object.defineProperty(t.prototype, "count", {
              get: function () {
                return this._items.length / 4;
              },
              enumerable: !1,
              configurable: !0,
            }),
            (t.prototype.invoke = function () {
              for (var t, e, n = [], o = 0; o < arguments.length; o++)
                n[o] = arguments[o];
              if (0 == this._flag) {
                this._flag = 1;
                for (var i = this._items, s = i.length, c = 0; c < s; c += 4)
                  if (0 != i[c + 3]) {
                    var a = i[c + 2];
                    null != a
                      ? (t = i[c]).call.apply(t, r([i[c + 1]], a, n))
                      : (e = i[c]).call.apply(e, r([i[c + 1]], n)),
                      2 == i[c + 3] && ((i[c + 3] = 0), (this._flag = 2));
                  }
                if (2 == this._flag) {
                  var u = i.length;
                  for (c = 0; c < u; )
                    0 != i[c + 3] ? (c += 4) : (i.splice(c, 4), (u -= 4));
                }
                this._flag = 0;
              }
            }),
            t
          );
        })();
        (n.Delegate = o), cc._RF.pop();
      },
      {},
    ],
    Emitter: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "b06bdH7wsVMDp4k9Kh4usTp", "Emitter"),
          Object.defineProperty(n, "__esModule", { value: !0 });
        var r = t("./Delegate"),
          o = t("./Event"),
          i = [],
          s = (function () {
            function t(e) {
              this._name = e || "Emitter_" + t._ID++;
            }
            return (
              (t.prototype.fire = function (t) {
                this._flag = 1;
                var e = this._listeners;
                if (!e) return !1;
                var n = e.count > 0;
                if (Array.isArray(t)) e.invoke.apply(e, t);
                else if (void 0 !== t) e.invoke(t);
                else if (t === o.default.EMPTY) {
                  var r = i.length > 0 ? i.pop() : new o.default();
                  e.invoke(r.setTo(this._name, this, this)),
                    (r.target = r.currentTarget = null),
                    i.push(r);
                } else e.invoke();
                return 2 == this._flag && this.clear(), (this._flag = 0), n;
              }),
              (t.prototype.on = function (t, e, n) {
                return 0 == arguments.length
                  ? this.on.bind(this)
                  : (1 == arguments.length && ((e = t), (t = null)),
                    this._listeners || (this._listeners = new r.Delegate()),
                    this._listeners.add(e, t, n),
                    this);
              }),
              (t.prototype.once = function (t, e, n) {
                return 0 == arguments.length
                  ? this.once.bind(this)
                  : (1 == arguments.length && ((e = t), (t = null)),
                    this._listeners || (this._listeners = new r.Delegate()),
                    this._listeners.once(e, t, n),
                    this);
              }),
              (t.prototype.off = function (t, e) {
                return (
                  1 == arguments.length && ((e = t), (t = null)),
                  this._listeners && this._listeners.remove(e, t),
                  this
                );
              }),
              (t.prototype.clear = function () {
                this._listeners &&
                  (1 == this._flag
                    ? (this._flag = 2)
                    : this._listeners.clear());
              }),
              (t.prototype.offAllCaller = function (t) {
                return (
                  t && this._listeners && this._listeners.clearForTarget(t),
                  this
                );
              }),
              (t._ID = 0),
              t
            );
          })();
        (n.default = s), cc._RF.pop();
      },
      { "./Delegate": "Delegate", "./Event": "Event" },
    ],
    Event: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "55ebfwni/ZM+5vzf7g2NvVJ", "Event"),
          Object.defineProperty(n, "__esModule", { value: !0 });
        var r = (function () {
          function t() {
            (this.touchId = 0), (this.delta = 0), (this.button = 0);
          }
          return (
            (t.prototype.setTo = function (t, e, n) {
              return (
                (this.type = t),
                (this.currentTarget = e),
                (this.target = n),
                this
              );
            }),
            (t.prototype.stopPropagation = function () {
              this._stopped = !0;
            }),
            Object.defineProperty(t.prototype, "touches", {
              get: function () {
                return this._touches;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "altKey", {
              get: function () {
                var t;
                return null === (t = this.nativeEvent) || void 0 === t
                  ? void 0
                  : t.altKey;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "ctrlKey", {
              get: function () {
                var t;
                return null === (t = this.nativeEvent) || void 0 === t
                  ? void 0
                  : t.ctrlKey;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "shiftKey", {
              get: function () {
                var t;
                return null === (t = this.nativeEvent) || void 0 === t
                  ? void 0
                  : t.shiftKey;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "metaKey", {
              get: function () {
                var t;
                return null === (t = this.nativeEvent) || void 0 === t
                  ? void 0
                  : t.metaKey;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "key", {
              get: function () {
                return this.nativeEvent.key;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "keyCode", {
              get: function () {
                return this.nativeEvent.keyCode;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "charCode", {
              get: function () {
                var t;
                return null === (t = this.nativeEvent) || void 0 === t
                  ? void 0
                  : t.code;
              },
              enumerable: !1,
              configurable: !0,
            }),
            Object.defineProperty(t.prototype, "keyLocation", {
              get: function () {
                return this.nativeEvent
                  ? this.nativeEvent.location || this.nativeEvent.keyLocation
                  : 0;
              },
              enumerable: !1,
              configurable: !0,
            }),
            (t.EMPTY = new t()),
            t
          );
        })();
        (n.default = r), cc._RF.pop();
      },
      {},
    ],
    GameBanner_item2: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "af67dkRedpG8J8Jrx+9P6cv", "GameBanner_item2");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            };
        Object.defineProperty(n, "__esModule", { value: !0 });
        var s = t("./AssetsManager"),
          c = cc._decorator,
          a = c.ccclass,
          u = c.property,
          l = (function (t) {
            function e() {
              var e = (null !== t && t.apply(this, arguments)) || this;
              return (e.icon = null), e;
            }
            return (
              o(e, t),
              (e.prototype.init = function (t) {
                var e = this;
                s.default.loadRemote(t.thumb, this, function (t, n) {
                  cc.isValid(e.node) &&
                    (e.icon.spriteFrame = new cc.SpriteFrame(n));
                });
              }),
              i([u(cc.Sprite)], e.prototype, "icon", void 0),
              i([a], e)
            );
          })(cc.Component);
        (n.default = l), cc._RF.pop();
      },
      { "./AssetsManager": "AssetsManager" },
    ],
    GameBanner_item: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "44da0n9EnBLkaIJjhPzxSeM", "GameBanner_item");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            };
        Object.defineProperty(n, "__esModule", { value: !0 });
        var s = t("./AssetsManager"),
          c = t("./ScrollListItem"),
          a = cc._decorator,
          u = a.ccclass,
          l = a.property,
          h = (function (t) {
            function e() {
              var e = (null !== t && t.apply(this, arguments)) || this;
              return (e.icon = null), e;
            }
            return (
              o(e, t),
              (e.prototype.start = function () {
                var t = this;
                (this.icon.node.width = YYGGames.isSquare() ? 150 : 200),
                  this.node.on(
                    cc.Node.EventType.MOUSE_LEAVE,
                    this.onTouchCancel,
                    this
                  ),
                  this.node.on(
                    cc.Node.EventType.MOUSE_ENTER,
                    this.onTouchStart,
                    this
                  ),
                  this.node.on(
                    cc.Node.EventType.TOUCH_END,
                    function () {
                      YYGGames.navigateWithForgame(
                        "GameBanner",
                        "MORE",
                        t.forgame
                      );
                    },
                    this
                  );
              }),
              (e.prototype.onTouchCancel = function (t) {
                var e = t.target;
                e && (e.scale = 1),
                  this.startGameScroll && this.startGameScroll();
              }),
              (e.prototype.onTouchStart = function (t) {
                var e = t.target;
                e && (e.scale = 1.05),
                  this.stoptGameScroll && this.stoptGameScroll();
              }),
              (e.prototype.setData = function (t) {
                var e = this;
                (this.forgame = t),
                  s.default.loadRemote(t.thumb, this, function (t, n) {
                    cc.isValid(e.node) &&
                      (e.icon.spriteFrame = new cc.SpriteFrame(n));
                  });
              }),
              (e.prototype.onItemRender = function (t, e, n) {
                this.setData(t, e, n);
              }),
              i([l(cc.Sprite)], e.prototype, "icon", void 0),
              i([u], e)
            );
          })(c.default);
        (n.default = h), cc._RF.pop();
      },
      {
        "./AssetsManager": "AssetsManager",
        "./ScrollListItem": "ScrollListItem",
      },
    ],
    GameBanner: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "eb1b6WTum5JBq5dGDk6j085", "GameBanner");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            },
          s =
            (this && this.__spreadArrays) ||
            function () {
              for (var t = 0, e = 0, n = arguments.length; e < n; e++)
                t += arguments[e].length;
              var r = Array(t),
                o = 0;
              for (e = 0; e < n; e++)
                for (var i = arguments[e], s = 0, c = i.length; s < c; s++, o++)
                  r[o] = i[s];
              return r;
            };
        Object.defineProperty(n, "__esModule", { value: !0 });
        var c = t("./GameBanner_item"),
          a = t("./ScrollList"),
          u = cc._decorator,
          l = u.ccclass,
          h =
            (u.property,
            (function (t) {
              function e() {
                var e = (null !== t && t.apply(this, arguments)) || this;
                return (e.name = "GameBanner"), e;
              }
              return (
                o(e, t),
                (e.prototype.start = function () {
                  var e = this;
                  t.prototype.start.call(this);
                  var n = YYGGames.forgames,
                    r = Array.from({ length: 10 }, function () {
                      return s(n);
                    }).flatMap(function (t) {
                      return t;
                    });
                  this.setDataList(r, 1, [10, 20, 10]),
                    (this.onMouseout = function () {
                      cc.isValid(e) &&
                        e._itemArr &&
                        e._itemArr.forEach(function (t) {
                          t.scale = 1;
                        });
                    });
                  var o = document.getElementById("GameCanvas");
                  o && o.addEventListener("mouseout", this.onMouseout),
                    setTimeout(function () {
                      e.scrollToLeft(0), e.scrollToRight(3e3);
                    }, 0.5);
                }),
                (e.prototype.onEnable = function () {
                  this.scrollToLeft(0), this.scrollToRight(3e3);
                }),
                (e.prototype.onDestroy = function () {
                  t.prototype.onDestroy.call(this);
                  var e = document.getElementById("GameCanvas");
                  e && e.removeEventListener("mouseout", this.onMouseout);
                }),
                (e.prototype.startGameScroll = function () {
                  this.scrollToRight(3e3);
                }),
                (e.prototype.stoptGameScroll = function () {
                  this.stopAutoScroll();
                }),
                (e.prototype.itemRender = function (t, e) {
                  if (this.onItemRender) this.onItemRender(t, e);
                  else {
                    (t.width = YYGGames.isSquare() ? 150 : 200),
                      (t.height = 150);
                    var n = t.getComponent(c.default);
                    n &&
                      ((n.startGameScroll = this.startGameScroll.bind(this)),
                      (n.stoptGameScroll = this.stoptGameScroll.bind(this)),
                      n.onItemRender(
                        this._dataArr[e],
                        e,
                        this._dataArr.length
                      ));
                  }
                  this.setPos(t, e);
                }),
                (e.prototype._onTouchBegan = function () {}),
                (e.prototype._onTouchMoved = function () {}),
                (e.prototype._onTouchEnded = function () {}),
                (e.prototype._onTouchCancelled = function () {}),
                i([l], e)
              );
            })(a.default));
        (n.default = h), cc._RF.pop();
      },
      { "./GameBanner_item": "GameBanner_item", "./ScrollList": "ScrollList" },
    ],
    GameBox: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "771ebfqvHJOH4fxMZbIOvV5", "GameBox");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            };
        Object.defineProperty(n, "__esModule", { value: !0 });
        var s = t("./AssetsManager"),
          c = cc._decorator,
          a = c.ccclass,
          u = c.property,
          l = (function (t) {
            function e() {
              var e = (null !== t && t.apply(this, arguments)) || this;
              return (
                (e.name = "GameBox"),
                (e.icon = null),
                (e.widget = null),
                (e.width = 200),
                (e.height = 200),
                e
              );
            }
            var n;
            return (
              o(e, t),
              (n = e),
              (e.prototype.start = function () {
                var t = this;
                (this.node.width = this.width),
                  (this.node.height = this.height);
                var e = this.forgames.shift();
                e
                  ? (s.default.loadRemote(e.thumb, this, function (e, n) {
                      cc.isValid(t.node) &&
                        ((t.icon.spriteFrame = new cc.SpriteFrame(n)),
                        t.widget.updateAlignment());
                    }),
                    this.node.on(
                      cc.Node.EventType.TOUCH_END,
                      function () {
                        YYGGames.navigateWithForgame("GameBox", "MORE", e);
                      },
                      this
                    ))
                  : (this.node.active = !1);
              }),
              Object.defineProperty(e.prototype, "forgames", {
                get: function () {
                  return (
                    (n.forgames && 0 != n.forgames.length) ||
                      (n.forgames = YYGGames.forgames.slice()),
                    n.forgames.sort(function () {
                      return 0.5 - Math.random();
                    }),
                    n.forgames
                  );
                },
                enumerable: !1,
                configurable: !0,
              }),
              i([u(cc.Sprite)], e.prototype, "icon", void 0),
              i([u(cc.Widget)], e.prototype, "widget", void 0),
              i([u()], e.prototype, "width", void 0),
              i([u()], e.prototype, "height", void 0),
              (n = i([a], e))
            );
          })(cc.Component);
        (n.default = l), cc._RF.pop();
      },
      { "./AssetsManager": "AssetsManager" },
    ],
    ScrollListItem: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "ac9e8AklIZJdozXG7jvu1FY", "ScrollListItem");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            };
        Object.defineProperty(n, "__esModule", { value: !0 });
        var s = cc._decorator,
          c = s.ccclass,
          a =
            (s.property,
            (function (t) {
              function e() {
                return (null !== t && t.apply(this, arguments)) || this;
              }
              return (
                o(e, t),
                (e.prototype.onItemRender = function (t) {
                  for (var e = [], n = 1; n < arguments.length; n++)
                    e[n - 1] = arguments[n];
                }),
                i([c], e)
              );
            })(cc.Component));
        (n.default = a), cc._RF.pop();
      },
      {},
    ],
    ScrollList: [
      function (t, e, n) {
        "use strict";
        cc._RF.push(e, "b614aG5yT9G06xSdkiwwPU1", "ScrollList");
        var r,
          o =
            (this && this.__extends) ||
            ((r = function (t, e) {
              return (r =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function (t, e) {
                    t.__proto__ = e;
                  }) ||
                function (t, e) {
                  for (var n in e)
                    Object.prototype.hasOwnProperty.call(e, n) && (t[n] = e[n]);
                })(t, e);
            }),
            function (t, e) {
              function n() {
                this.constructor = t;
              }
              r(t, e),
                (t.prototype =
                  null === e
                    ? Object.create(e)
                    : ((n.prototype = e.prototype), new n()));
            }),
          i =
            (this && this.__decorate) ||
            function (t, e, n, r) {
              var o,
                i = arguments.length,
                s =
                  i < 3
                    ? e
                    : null === r
                    ? (r = Object.getOwnPropertyDescriptor(e, n))
                    : r;
              if (
                "object" == typeof Reflect &&
                "function" == typeof Reflect.decorate
              )
                s = Reflect.decorate(t, e, n, r);
              else
                for (var c = t.length - 1; c >= 0; c--)
                  (o = t[c]) &&
                    (s = (i < 3 ? o(s) : i > 3 ? o(e, n, s) : o(e, n)) || s);
              return i > 3 && s && Object.defineProperty(e, n, s), s;
            };
        Object.defineProperty(n, "__esModule", { value: !0 }),
          (n.SCROLL_VERTICAL = n.SCROLL_HORIZONTAL = void 0);
        var s = t("./ScrollListItem");
        (n.SCROLL_HORIZONTAL = 1), (n.SCROLL_VERTICAL = 2);
        var c = cc._decorator,
          a = c.ccclass,
          u = c.property,
          l = (function (t) {
            function e() {
              var e = (null !== t && t.apply(this, arguments)) || this;
              return (
                (e.itemPrefab = null),
                (e._numItem = 0),
                (e._itemArr = []),
                (e._itemIndex = 0),
                (e._dataIndex = 0),
                (e._direction = 0),
                e
              );
            }
            return (
              o(e, t),
              (e.prototype.start = function () {
                this.node.on("scrolling", this.scrollCheck, this);
              }),
              (e.prototype.onDestroy = function () {
                this.node && this.node.off("scrolling", this.scrollCheck, this);
              }),
              (e.prototype.setDataList = function (t, e, r) {
                void 0 === e && (e = n.SCROLL_VERTICAL),
                  (this._dataArr = t),
                  (this._direction = e),
                  (this._gapNum = r),
                  this.createItem();
              }),
              (e.prototype.createItem = function () {
                var t = this.node.height;
                if (!this._itemSize) {
                  var e = cc.instantiate(this.itemPrefab);
                  this._direction == n.SCROLL_HORIZONTAL
                    ? ((this._itemSize = YYGGames.isSquare() ? 150 : 200),
                      (t = cc.view.getCanvasSize().width + 200))
                    : (this._itemSize = e.height),
                    e.destroy();
                }
                (this._numItem = Math.floor(t / this._itemSize) + 2),
                  this._dataArr.length < this._numItem &&
                    (this._numItem = this._dataArr.length),
                  (this._itemArr.length = 0);
                for (var r = 0; r < this._numItem; r++)
                  ((e = cc.instantiate(this.itemPrefab)).parent = this.content),
                    this._itemArr.push(e),
                    this.itemRender(e, r);
                var o = this._itemSize * this._dataArr.length;
                this._gapNum && this._gapNum[0] && (o += this._gapNum[0]),
                  this._gapNum && this._gapNum[1] && (o += this._gapNum[1]),
                  this._gapNum &&
                    this._gapNum[2] &&
                    (o += this._gapNum[2] * (this._dataArr.length - 1)),
                  this._direction == n.SCROLL_HORIZONTAL
                    ? (this.content.width = o)
                    : (this.content.height = o),
                  (this._itemIndex = this._dataIndex =
                    this._itemArr.length - 1);
              }),
              (e.prototype.scrollCheck = function () {
                var t = this.getScrollOffset().y,
                  e = (this._dataIndex + 1 - this._numItem) * this._itemSize;
                this._gapNum && this._gapNum[0] && (e += this._gapNum[0]),
                  this._gapNum &&
                    this._gapNum[2] &&
                    (e +=
                      this._gapNum[2] * (this._dataIndex + 1 - this._numItem));
                var r = this._itemSize;
                if (
                  (this._direction == n.SCROLL_HORIZONTAL &&
                    ((t = this.getScrollOffset().x),
                    (e = -e),
                    (r = -this._itemSize)),
                  (this._direction == n.SCROLL_VERTICAL && t > r + e) ||
                    (this._direction == n.SCROLL_HORIZONTAL && t < r + e))
                ) {
                  if ((i = this._dataIndex + 1) >= this._dataArr.length) return;
                  this._dataIndex = i;
                  var o = this._itemIndex + 1;
                  o >= this._numItem && (o = 0),
                    (s = this._itemArr[o]) && this.itemRender(s, i),
                    (this._itemIndex = o);
                } else if (
                  (this._direction == n.SCROLL_VERTICAL && t < e) ||
                  (this._direction == n.SCROLL_HORIZONTAL && t > e)
                ) {
                  var i, s;
                  if ((i = this._dataIndex + 1 - this._numItem - 1) < 0) return;
                  this._dataIndex--,
                    (s = this._itemArr[this._itemIndex]) &&
                      this.itemRender(s, i),
                    this._itemIndex--,
                    this._itemIndex < 0 &&
                      (this._itemIndex = this._numItem - 1);
                }
              }),
              (e.prototype.itemRender = function (t, e) {
                if (this.onItemRender) this.onItemRender(t, e);
                else {
                  var n = t.getComponent(s.default);
                  n &&
                    n.onItemRender(this._dataArr[e], e, this._dataArr.length);
                }
                this.setPos(t, e);
              }),
              (e.prototype.setPos = function (t, e) {
                var r = this.countPosByIndex(e);
                this._direction == n.SCROLL_HORIZONTAL
                  ? t.setPosition(r, 0)
                  : t.setPosition(0, -r);
              }),
              (e.prototype.countPosByIndex = function (t) {
                var e = (0.5 + t) * this._itemSize;
                return (
                  this._gapNum && this._gapNum[0] && (e += this._gapNum[0]),
                  this._gapNum && this._gapNum[2] && (e += this._gapNum[2] * t),
                  e
                );
              }),
              (e.prototype.scroll2Index = function (t) {
                this.stopAutoScroll(),
                  t > this._dataArr.length - this._numItem - 2 &&
                    (t = this._dataArr.length - this._numItem - 2),
                  t < 0 && (t = 0);
                var e = this.countPosByIndex(t) - 0.5 * this._itemSize,
                  r = new cc.Vec2(0, -e);
                this._direction == n.SCROLL_HORIZONTAL &&
                  (r = new cc.Vec2(e, 0)),
                  this.scrollToOffset(r, 0.5);
              }),
              i(
                [
                  u({
                    type: cc.Prefab,
                    tooltip: "item\u5b50\u8282\u70b9\u9884\u5236\u4f53",
                  }),
                ],
                e.prototype,
                "itemPrefab",
                void 0
              ),
              i([a], e)
            );
          })(cc.ScrollView);
        (n.default = l), cc._RF.pop();
      },
      { "./ScrollListItem": "ScrollListItem" },
    ],
  },
  {},
  [
    "AssetsManager",
    "GameBanner",
    "GameBanner_item",
    "GameBanner_item2",
    "GameBox",
    "ScrollList",
    "ScrollListItem",
    "Delegate",
    "Emitter",
    "Event",
  ]
);
