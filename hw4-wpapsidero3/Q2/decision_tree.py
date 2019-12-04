from util import entropy, information_gain, partition_classes
import numpy as np 
import ast
from numbers import Number

class DecisionTree(object):
    def __init__(self):
        # Initializing the tree as an empty dictionary or list, as preferred
        self.leaf_size = 10
        self.depth = 6
        self.temp_tree = [] 
        self.tree = []

    def  attribute_selection(ans,X,y):       
        len_X = len(X[0])
        comp = - 1.0 
        
        for index in range(len_X):
            if isinstance(X[0][index], Number):
                values = np.mean([ x[index] for x in X ])
                lx, rx, ly, ry = partition_classes(X,y,index,values)
                t_gain = information_gain(y,[ly,ry])
                if t_gain > comp:
                    comp = t_gain
                    big_values = values
                    big_index =  index
    
            else : 
                lv = set([x[index] for x in X])
                for values in lv:
                    lx,rx, ly, ry = partition_classes(X,y,index,values)
                    t_gain = information_gain(y,[ly,ry])
                    if t_gain > comp:
                        comp = t_gain
                        big_values = values
                        big_index =  index
        return big_index, big_values

    def tree_build(ans,att,val):
        ans.depth += ans.depth
        if len(val) < ans.leaf_size or ans.depth > 30 or len(set(val)) ==1:
            return [-1, int(round(np.mean(val))), -1, -1]
        split_attribute, split_val = ans.attribute_selection(att,val)
        xl, xr, yl, yr = partition_classes(att, val, split_attribute, split_val)
        rl = ans.tree_build(xr, yr)
        tl = ans.tree_build(xl, yl)
        return [split_attribute, split_val, tl, rl]

    def learn(self, X, y):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree
        
        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a 
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value)
        self.tree = self.tree_build(X,y)

    def first_class(att, pre_record):
        if att.temp_tree[0]== -1:
            return att.temp_tree[1]
        if isinstance(pre_record[att.temp_tree[0]], Number):
            if pre_record[att.temp_tree[0]] <= att.temp_tree[1]:
                att.temp_tree = att.temp_tree[2]
                return att.first_class(pre_record)
            else :     
                att.temp_tree = att.temp_tree[3]
                return att.first_class(pre_record)
        else : 
            if pre_record[att.temp_tree[0]] ==  att.temp_tree[1]:
                att.temp_tree = att.temp_tree[2]
                return att.first_class(pre_record)
            else :     
                att.temp_tree = att.temp_tree[3]
                return att.first_class(pre_record)

    def classify(self, record):
        # TODO: classify the record using self.tree and return the predicted label
        self.temp_tree = self.tree
        return self.first_class(record)
