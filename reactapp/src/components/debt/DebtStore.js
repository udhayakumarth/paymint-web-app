import {createStore,action,thunk,computed} from 'easy-peasy'; 
import { baseUrl } from '../../api/config';
import axios from 'axios';

export default createStore({
    debts:[],
    setDebts:action((state,payload)=>{
        state.debts=payload;
    }),
    amount:0,
    setAmount:action((state,payload)=>{
        state.amount=payload;
    }),  
    dueDate:'',
    setdueDate:action((state,payload)=>{
        state.dueDate=payload;
    }),
    moneyFrom:'',
    setMoneyFrom:action((state,payload)=>{
        state.moneyFrom=payload;
    }),
    status:'unpaid',
    setStatus:action((state,payload)=>{
        state.status=payload;
    }),
    debtCount:computed((state)=>state.debts.length),
    debtPending:computed((state)=>state.debts.filter((debt)=>debt.status==='unpaid').length),
    getDebtId:computed((state)=>{
        return (id)=> state.debts.find((debt)=>(debt.debtId).toString()===id)
    }),


    addDebt:thunk(async (action,body,helpers)=>{
      const {debts}=helpers.getState();
      console.log(body);

      try{
          const response= await axios.post(`${baseUrl}/debts`,body,{
            headers: { Authorization: `Bearer ${body.token}` }
        })
          console.log(response.data.data); 
          // response.data will return whole json object along with message so adding another data points to the actual data
          action.setDebts([...debts,response.data.data]);
          console.log(debts);
          console.log("Added ");
          action.setAmount('');
          action.setStatus('unpaid'); 
          action.setMoneyFrom('');
          action.setdueDate('');
          // history('/');
        }
        catch(err){
          console.log(`Error: ${err.message}`)
        }
    }),
    delDebt:thunk(async (action,body,helpers)=>{
      const {debts}=helpers.getState();
      console.log(body)

      try {
        
        await axios.delete(`${baseUrl}/debts?debtId=${body.debtId}`,
        {
          headers: { Authorization: `Bearer ${body.token}` }
        });
        action.setDebts(debts.filter(debt=>debt.debtId!==body.debtId));
        console.log('Deleted : '+ body.debtId);
        // history('/');
      } catch (err) {
        console.log(`Error: ${err.message}`)
      }
    }),

    editDebt:thunk(async (action, body,helpers)=>{
      const { debts } = helpers.getState();
      const { debtId } = body;
      console.log(debtId+""+typeof(debtId));
      console.log(body.token);

      try{
        const response=await axios.put(`${baseUrl}/debts`,
        body,
        {
          headers: { Authorization: `Bearer ${body.token}` }
        });
        if(response && response.data) {
          action.setDebts(
            debts.map((debt)=>debt.debtId===debtId?{...body}:debt)
          )
          console.log("Edited");
          action.setAmount('');
          action.setStatus('unpaid'); 
          action.setMoneyFrom('');
          action.setdueDate(new Date());
        }
        else {
          console.log("Error: Invalid response");
        }
      }
      catch (err){
        console.log(err.response ? err.response.data : err.message);
      }
    }),
    getData: thunk(async (action, token, helpers) => {
      console.log(token);
      try {
        const response = await axios.get(`${baseUrl}/debts/user`,{
          headers: { Authorization: `Bearer ${token}` }
      });
        console.log(response.data);
        if (response && response.data) {
          action.setDebts(response.data);
          console.log("Data fetched successfully");
        } else {
          console.log("Error: Invalid response");
        }
      } catch (err) {
        console.log(`Error: ${err.message}`);
      }
    })  
})