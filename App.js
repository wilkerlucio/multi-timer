import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { foo } from 'shadow-cljs/demo.app';

export default class App extends React.Component {
  render() {
    return foo();
  }
}

