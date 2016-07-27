import {
	NativeModules
} from 'react-native';
import {
	Component
} from 'react';

let Locating = {
	startObserving: function () {
		return NativeModules.YPAMapLocation.startObserving();
	},

	stopObserving: function () {
		return NativeModules.YPAMapLocation.stopObserving();
	},

	getCurrentPosition: function (options) {
		return NativeModules.YPAMapLocation.getCurrentPosition(options);
	},

	configure: function (options) {
		this.options = options;
	},
}
export default Locating;
