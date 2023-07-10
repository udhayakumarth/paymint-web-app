import React, { useState } from 'react';
import { useDisclosure } from '@mantine/hooks';
import { Modal, Badge, Table, Button, TextInput, Title, Switch, useMantineTheme ,Notification} from '@mantine/core';
import { FaTrash,FaEdit,FaMoneyBill,FaUser,FaCalendarAlt } from 'react-icons/fa';
import { useStoreActions, useStoreState } from 'easy-peasy';
import { DatePickerInput } from '@mantine/dates';
import { useSelector } from 'react-redux';

const DebtList = () => {
  const token  = useSelector(state => state.user.token)
  // console.log(token);
  const debts = useStoreState((state) => state.debts);
  // console.log(debts);
  const amount=useStoreState((state)=>state.amount)
  const moneyFrom=useStoreState((state)=>state.moneyFrom)
  const dueDate=useStoreState((state)=>state.dueDate)
  const status=useStoreState((state)=>state.status)

  const setAmount=useStoreActions((action)=>action.setAmount)
  const setMoneyFrom=useStoreActions((action)=>action.setMoneyFrom)
  const setdueDate=useStoreActions((action)=>action.setdueDate)
  const setStatus=useStoreActions((action)=>action.setStatus)
  const delDebt=useStoreActions((action)=>action.delDebt)
  const editDebt=useStoreActions((action)=>action.editDebt)
  const [opened, { open, close }] = useDisclosure(false);
  const theme = useMantineTheme();

  const [debtId,setDebtId]=useState('');
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedDebt, setSelectedDebt] = useState(null);
  
  const [upNot,setupNot]=useState(false);
  const [delNot,setdelNot]=useState(false);


  const handleOpenModal = (e,debtList) => {
    e.preventDefault();
    console.log(debtList);
    open();
    setAmount(`${debtList.amount}`);
    setdueDate(`${debtList.dueDate}`);
    setMoneyFrom(`${debtList.moneyFrom}`);
    setStatus(`${debtList.status}`)
    setDebtId(debtList.debtId);
  };

  const handleViewOpenModal = (e, debtList) => {
    e.preventDefault();
    // setAmount(`${debtList.amount}`);
    // setdueDate(`${debtList.dueDate}`);
    // setMoneyFrom(`${debtList.moneyFrom}`);
    // setStatus(`${debtList.status}`);
    // setDebtId(`${debtList.debtId}`);
    setSelectedDebt(debtList);
    setViewModalOpen(true);
  };
  const handleCloseViewModal = () => {
    setViewModalOpen(false);
  };  

  const handleSaveModal = (debtId) => {
    console.log(debtId);
    const Udebt = {
      debtId: debtId,
      amount: amount,
      dueDate: dueDate,
      moneyFrom: moneyFrom,
      status: status,
    };
    console.log(debtId);
    console.log(typeof(debtId));
    editDebt({...Udebt,token:token});
    setSelectedDebt(Udebt);
    close();
    setupNot(true);
    setTimeout(()=>{
      setupNot(false)
    },1000);
  };
  

  const handleDelete = (e, debtId) => {
    e.preventDefault();
    delDebt({debtId:debtId,token:token});
    
    setViewModalOpen(false);
    // console.log(`${debtId} deleted`);
    // Perform your delete logic here
    setdelNot(true);
    setTimeout(()=>{
      setdelNot(false)
    },1000);
  };
  const handleSwitchToggle = () => {
    const newStatus = status === 'paid' ? 'unpaid' : 'paid';
    setStatus(newStatus);
    console.log(status);
  };

  const rows = debts.map((element) => (
    <tr key={element.debtId} style={{ fontWeight: "bold",textAlign:"left" }}>
      <td>{element.moneyFrom}</td>
      <td>{element.dueDate}</td>
      <td>{`Rs. ${element.amount}`}</td>
      <td>
        <Badge color={element.status === 'paid' ? "green" : "red"} size="lg" variant="outline">{element.status}</Badge>
      </td>
      <td>
        <Button variant='light' style={{ color: 'grey', background: 'rgba(217, 214, 210,0.2)' }} onClick={(e) => { handleViewOpenModal(e, element) }}><b>view</b></Button>
      </td>
    </tr>
  ));
  

  return (
  <div style={{ width: '80%', marginTop: '20px'}}>
    <Table horizontalSpacing="xl" verticalSpacing="md" fontSize="lg" highlightOnHover>
     <thead>
      <tr style={{ textAlign: 'left'}}>
            <th style={{color:"grey"}}>
                FROM
              </th>
              <th style={{color:"grey"}}>
                AMOUNT
              </th>
              <th style={{color:"grey"}}>
                DUE DATE
              </th>
              <th style={{color:"grey"}}>
                STATUS
              </th>
              <th style={{color:"grey"}}>
                DETAILS
              </th>
        </tr>
      </thead>
      <tbody>{rows}</tbody>
    </Table>
          <Modal
            opened={opened}
            onClose={close}
            centered
            position="center" // Center the Modal on the page
            title={
              <Title>
                <span>Edit</span>
              </Title>
            }
            size="300px"
            radius="lg"
            zIndex={1001} // Set a higher zIndex value for the Edit Modal

            // transitionProps={{ transition: 'fade', duration: 500, timingFunction: 'linear' }}
            overlayProps={{
              color: theme.colorScheme === 'dark' ? theme.colors.dark[9] : theme.colors.gray[2],
              opacity: 0.5,
              blur: 1,
            }}            
          >
            <div>
            <DatePickerInput
              radius="md"
              style={{marginTop: "2px"}}
              mx="auto"
              maw={400}
              label="Due date"
              value={new Date(dueDate)}
              onChange={(date) => {
                // Convert the selected date back to the desired format
                const formattedDate = date.toLocaleDateString('en-US', {
                  month: 'short',
                  day: 'numeric',
                  year: 'numeric',
                });
                setdueDate(formattedDate);
              }}
                          // dropdownType='popover'
              icon={<FaCalendarAlt size="1.1rem" stroke={1.5} />}

            />


            <TextInput radius="md" style={{marginTop:6}}
                // withAsterisk
                label="From"
                value={moneyFrom}
                data-autofocus
                onChange={(event) => setMoneyFrom(event.currentTarget.value)}
                icon={<FaUser size="1.1rem" stroke={1.5}/>}
              />
              <TextInput radius="md" style={{marginTop:6}}
                // withAsterisk 
                label="Amount"
                value={amount}
                placeholder=""
                onChange={(event) => setAmount(event.currentTarget.value)}
                icon={<FaMoneyBill size="1.1rem" stroke={1.5}/>}           
              />
              {/* <TextInput radius="md"
                style={{marginTop: 6}}
                // withAsterisk 
                label="Due date"
                value={dueDate}
                onChange={(event) => setdueDate(event.currentTarget.value)}
              /> */}
              <div radius="md"
                style={{marginTop: "6px"}}>
                <Switch 
                  style={{width:"80px"}} checked={status==='paid'}
                  onChange={handleSwitchToggle} size="md"
                  onLabel={<span style={{fontSize:"13px"}}><b> &nbsp; paid</b></span>} 
                  offLabel= {<span style={{fontSize:"13px",color:"rgb(51, 154, 240)"}}><b>unpaid</b></span>} />
                {/* <span>{status === 'paid' ? 'paid' : 'unpaid'}</span> */}
              </div>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '16px' }}>
              <Button variant="subtle" onClick={close} fullWidth style={{ marginLeft: '10px', width: '45%' }} >
                Cancel
              </Button>
              <Button onClick={() => handleSaveModal(debtId)}fullWidth style={{ marginRight: '10px', width: '45%' }}>
                Update
              </Button>
            </div>
          </Modal>

          <Modal
            opened={viewModalOpen}
            onClose={handleCloseViewModal}
            centered
            radius="lg"
    
            overlayProps={{
              color: theme.colorScheme === 'dark' ? theme.colors.dark[9] : theme.colors.gray[2],
              opacity: 0.5,
              blur: 2,
            }} 
            size="400px"
            position="center" 
          >

            {selectedDebt && setViewModalOpen && (
              <div style={{fontSize:"18px"}}>
                <div style={{marginLeft:"100px",marginBottom:"25px"}}>
                  <span><h2>Details</h2></span>
                  <p><b>{`Amount :`}</b>{selectedDebt.amount}</p>
                  <p><b>{`Due :`}</b>{selectedDebt.dueDate}</p>
                  <p><b>{`To :`}</b>{selectedDebt.moneyFrom}</p>
                  <p><b>{`Status :`}</b>{selectedDebt.status}</p>
                </div>
                {/* Button group */}
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  
                  <Button
                    variant="light"
                    // style={{ background: 'rgba(22, 222, 175, 0.9)', color: 'white' }}
                    onClick={(e) => {
                      handleOpenModal(e,selectedDebt);
                    }}
                  >
                    <FaEdit/> &nbsp;
                    Edit
                  </Button>
                  <Button
                    // style={{ background: 'rgba(222, 22, 86, 0.7)', color: 'white' }}
                    onClick={(e) => {
                      handleDelete(e, selectedDebt.debtId);
                    }}
                  >
                    <FaTrash /> &nbsp;
                    Delete
                  </Button>
                </div>
              </div>
            )}
          </Modal>
          {upNot &&
            <Notification
              transition="slide-up"
              title="The Debt has been Edited Successfully !!!"
              color="blue"
              icon={<FaEdit />}
              style={{ position: 'fixed', bottom: '30px', right: '30px' }}
            />}

          {delNot &&
            <Notification
              transition="slide-up"
              title="A Debt has been deleted Successfully !!!"
              color="red"
              icon={<FaTrash />}
              style={{ position: 'fixed', bottom: '30px', right: '30px' }}
            />}

    </div>
  )
}

export default DebtList;