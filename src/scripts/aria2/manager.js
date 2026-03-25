(() => {
	"use strict";

	angular.module("ariaNg").factory("aria2", [
		// to D:\program\Capacitor\AriaNg-master\src\scripts\core\root.js
		function () {
			/** @type {import('@capacitor/core').CapacitorGlobal} */
			var capacitor = window.Capacitor;
			console.log(capacitor.getPlatform());
			var aria2 = capacitor.Plugins.Aria2;
			// /** @type {import('@capacitor/preferences').PreferencesPlugin} */
			// var Preferences = capacitor.Plugins.Preferences;
			// const setName = async function () {
			// 	await Preferences.set({
			// 		key: "name",
			// 		value: "Max",
			// 	});
			// };
			// await setName();
			return {
				manager: aria2,
				init: async function () {
					if (capacitor.getPlatform() === "web") {
						console.log("web platform, skipping aria2 init");
					} else {
						await aria2.start();
					}
				},
			};
		},
	]);
})();
