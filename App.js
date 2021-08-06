/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState} from 'react';

import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  NativeModules,
} from 'react-native';

const App = () => {
  const [err, setErr] = useState();
  const [data, setData] = useState();

  const openActivity = () => {
    (async () => {
      try {
        const resp = await NativeModules.OpenActivity.authorizeVenmo();
        console.log({resp});
        setData(resp.username);
      } catch (e) {
        console.log({e});
        setErr(e.message);
      }
    })();
  };

  return (
    <View style={styles.container}>
      <Text style={{color: 'white'}}>{data}</Text>
      <Text style={{color: 'red'}}>{err}</Text>
      <TouchableOpacity style={styles.button} onPress={openActivity}>
        <Text style={{color: 'white'}}>Authorize Venmo</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    height: 40,
    width: 160,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'blue',
  },
});

export default App;
