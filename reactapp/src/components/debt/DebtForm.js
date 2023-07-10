import React, { useState } from 'react';
import { useDisclosure } from '@mantine/hooks';
import { FaPlus,FaMoneyBill,FaUser,FaCalendarAlt } from 'react-icons/fa';
import { Modal, Group, Button, TextInput, Title,useMantineTheme,Notification } from '@mantine/core';
import { DatePickerInput } from '@mantine/dates';
import { useStoreActions, useStoreState } from 'easy-peasy';
import { useSelector } from 'react-redux';

function DebtForm() {
  const token  = useSelector(state => state.user.token);

  const [opened, { open, close }] = useDisclosure(false);
  const amount=useStoreState((state)=>state.amount)
  const moneyFrom=useStoreState((state)=>state.moneyFrom)
  const dueDate=useStoreState((state)=>state.dueDate)
  const status = useStoreState((state) => state.status);

  const setAmount=useStoreActions((action)=>action.setAmount)
  const setMoneyFrom=useStoreActions((action)=>action.setMoneyFrom)
  const setdueDate=useStoreActions((action)=>action.setdueDate)
  // const setStatus=useStoreActions((action)=>action.setStatus)
  const addDebt=useStoreActions((action)=>action.addDebt)
  const theme = useMantineTheme();
  const [newNot,setnewNot]=useState(false);

  const handleOpenModal = () => {
    open();
    setAmount('');
    setdueDate(new Date());
    setMoneyFrom('');
    // setStatus('unpaid');
  };

  const handleSaveModal = async (e) => {
    e.preventDefault();
    const dueDate1 = dueDate.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
    const NDebt = {
      amount: amount,
      dueDate: dueDate1,
      moneyFrom: moneyFrom,
      status: status,
    };
    console.log(NDebt);
    addDebt({...NDebt,token:token});
    // console.log("Added")
    close();
    setnewNot(true);
    setTimeout(()=>{
      setnewNot(false)
    },1000);

  };
  
  // const handleSwitchToggle = () => {
  //   const newStatus = status === 'paid' ? 'unpaid' : 'paid';
  //   setStatus(newStatus);
  //   console.log(status);
  // };
  return (
    <>
      <Modal
        opened={opened}
        onClose={close}
        centered
        position="center" // Center the Modal on the page

        title={
          <Title size="32" style={{textAlign:"center"}}>
            Add debt
          </Title>
        }
        size="350px"
        padding="35px"
        radius="lg"
        overlayProps={{
          color: theme.colorScheme === 'dark' ? theme.colors.dark[9] : theme.colors.gray[2],
          opacity: 0.55,
          blur: 3,
        }}        
      >
        <div>
          <DatePickerInput
              radius="md"
              style={{marginTop: "2px"}}
              mx="auto"
              maw={400}
              label="Due date"
              value={dueDate}
              onChange={setdueDate}
              // dropdownType='popover'
              icon={<FaCalendarAlt size="1.1rem" stroke={1.5} />}

            />

          <TextInput radius="md" style={{marginTop:"7px"}}
              // withAsterisk
              label="From"
              value={moneyFrom}
              data-autofocus
              onChange={(event) => setMoneyFrom(event.currentTarget.value)}
              icon={<FaUser size="1.1rem" stroke={1.5}/>}
            />
            <TextInput radius="md" style={{marginTop:"7px"}}
              // withAsterisk 
              label="Amount"
              value={amount}
              placeholder=""
              onChange={(event) => setAmount(event.currentTarget.value)}
              icon={<FaMoneyBill size="1.1rem" stroke={1.5}/>}
            />

            {/* <TextInput
              radius="md"
              style={{marginTop: "6px"}}
              type="date"
              label="Due date"
              value={dueDate}
              onChange={setdueDate}
              icon={<FaCalendarAlt size="1.1rem" stroke={1.5} />}

            /> */}

            {/* <div radius="md"
              style={{marginTop: "6px"}}>
              <Switch style={{width:"80px"}} onChange={handleSwitchToggle} size="md" onLabel={<span style={{fontSize:"13px"}}><b>paid</b></span>} offLabel= {<span style={{fontSize:"13px",color:"rgb(51, 154, 240)"}}><b>unpaid</b></span>} />
              {/* <span>{status === 'paid' ? 'Paid' : 'Unpaid'}</span> */}
            {/* </div> */}
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '16px' }}>
          <Button variant="subtle" onClick={close} fullWidth style={{ marginLeft: '10px', width: '45%' }} >
            Cancel
          </Button>
          <Button onClick={handleSaveModal} fullWidth style={{ marginRight: '10px', width: '45%' }}>
            Save
          </Button>
        </div>

      </Modal>
      {newNot &&
        <Notification
          transition="slide-up"
          title="A New Debt has been Added Successfully !!!"
          color='green'
          icon={
              <FaPlus />}
          style={{ position: 'fixed', bottom: '30px', right: '30px' }}
        />
      }
      <Group position="center" style={{marginLeft:"50px"}}>
        <Button onClick={handleOpenModal} style={{width:"170px"}}>
          New Debt
          <FaPlus style={{marginLeft:"15px"}}/>
        </Button>
      </Group>
    </>
  );
}

export default DebtForm;
