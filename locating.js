import {
	NativeModules
} from 'react-native';
import {
	Component
} from 'react';

export default class Locating extends Component {
	startObserving() {
		return NativeModules.YPAmapLocation.startObserving();
	}
	
	stopObserving() {
		return NativeModules.YPAmapLocation.stopObserving();
	}
	
	getCurrentPosition(options) {
		return NativeModules.YPAmapLocation.getCurrentPosition(options);
	}
	
	configure(options) {
		this.options = options;
	}
}