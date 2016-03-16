/*globals require, exports */
/**
 * @class Heap
 */
var _ = require('underscore');


function Heap(arr, lessOrEqualComparator) {
	var heap = [undefined];

	var costFunction = costFunction || function (el) {
		return el;
	};

	lessOrEqualComparator = lessOrEqualComparator || function (anEl, anotherEl) {
		return anEl <= anotherEl;
	};



	this.top = function top() {
		return heap[1];
	};


	this.pop = function pop() {
		var intDivisionIbyTwo;
		var n = heap.length;
		var i = 1;
		var el = heap[1];

		if (n === 1) {
			throw new Error("Popping empty stack");
		}

		//pull up
		while ((2 * i + 1) < n) {
			if (lessOrEqualComparator(heap[2 * i], heap[2 * i + 1])) {
				heap[i] = heap[2 * i];
				i = i * 2;
			} else {
				heap[i] = heap[2 * i + 1];
				i = i * 2 + 1;
			}
		}

		//fill bubble
		if (i != n - 1) {
			heap[i] = heap[n - 1];
			while (i > 1) {
				intDivisionIbyTwo = i >> 1;
				if (lessOrEqualComparator(heap[i], heap[intDivisionIbyTwo])) {
					var tmp = heap[i];
					heap[i] = heap[intDivisionIbyTwo];
					heap[intDivisionIbyTwo] = tmp;
				}
				i = intDivisionIbyTwo;
			}
		}

		heap.pop();
		return el;
	};

	this.push = function push(el) {
		var intDivisionIbyTwo;
		var n = heap.length;
		heap.push(el);

		for (var i = n; i > 1; i >>= 1) {
			intDivisionIbyTwo = i >> 1;
			if (lessOrEqualComparator(heap[i], heap[intDivisionIbyTwo])) {
				var tmp = heap[i];
				heap[i] = heap[intDivisionIbyTwo];
				heap[intDivisionIbyTwo] = tmp;
			}
		}
	};

	Object.defineProperty(this, 'size', {
		get: function () {
			return heap.length;
		},
		set: function(){
			return false;
		},
		enumerable: false,
		configurable: false
	});

	arr.forEach(function (object) {
		this.push(object);
	}.bind(this));

}

exports.Heap = Heap;
exports.create = function(arr, lessOrEqualComparator){
	arr = arr || [];
	if(_.isFunction(arr)) {
		lessOrEqualComparator = arr;
		arr = [];
	}
	return new Heap(arr, lessOrEqualComparator);
};