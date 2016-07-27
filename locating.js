import {
	NativeModules
} from 'react-native';
import {
	Component
} from 'react';

export default class Locating extends Component {
	startObserving() {
		NativeModules.YPAmapLocation.startObserving();
	}
	
	stopObserving() {
		NativeModules.YPAmapLocation.stopObserving();
	}
	
	getCurrentPosition(options, success, error) {
		NativeModules.YPAmapLocation.getCurrentPosition(options, success, error);
	}
	
	configure(options) {
		
	}
}