(() => {
	"use strict";

	angular.module("ariaNg").factory("aria2", [
		// to D:\program\Capacitor\AriaNg-master\src\scripts\core\root.js
		function () {
			/** @type {import('@capacitor/core').CapacitorGlobal} */
			var capacitor = window.Capacitor;
			var aria2 = capacitor.Plugins.Aria2;
			var is_init = false;
			var initCallBack = [];

			async function triggerInitCallBack() {
				await Promise.all(
					initCallBack.map(async (callback) => {
						await callback();
						console.log("triggerInitCallBack", callback);
					})
				);
				console.log("triggerInitCallBack", initCallBack.length, initCallBack);
			}
			console.log(capacitor.getPlatform());
			if (capacitor.getPlatform() === "web") {
				return {
					manager: aria2,
					isinit: function () {
						return false;
					},
					init: function () {
						console.log("web platform, skipping aria2 init");
					},
					addInitCallBack: function (callback) {
						console.log("web platform callback: ", callback);
					},
					changeConfig: function (config, restart) {
						console.log("web platform changeConfig: ", config, restart);
					},
				};
			}
			return {
				manager: aria2,
				isinit: function () {
					return is_init;
				},
				init: async function () {
					await aria2.start();
					is_init = true;
					await triggerInitCallBack();
				},
				addInitCallBack: function (callback) {
					if (is_init) {
						return callback();
					}
					initCallBack.push(callback);
				},
				changeConfig: function (config, restart) {
					aria2.configure({
						config: config,
						restart: restart ?? false,
					});
				},
			};
		},
	]);
})();
