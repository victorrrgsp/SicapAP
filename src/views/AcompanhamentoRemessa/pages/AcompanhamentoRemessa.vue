<template>
   <div>
     <b-table striped hover responsive 
         id="my-table"
        :items="tableData" 
        :fields="columns" 
        :per-page="perPage"
        :current-page="currentPage"
        aria-controls="my-table"
        small
        >
        <template #cell(index)="data">{{ data.index + 1 }}</template>

        <template #cell(opcao)="data">
            <b-icon icon="pencil-fill" 
                cursor= "pointer" 
                @click="editItem(data.item)" pill 
                variant="primary" 
                size="sm">
            </b-icon>
                &nbsp;
            <b-icon icon="trash" 
                cursor= "pointer" 
                @click="deleteItem(data.item)" pill 
                v-b-modal="'edit-modal'" 
                variant="primary" 
                size="sm">
            </b-icon>
        </template>
     </b-table>

 <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>

    <p class="mt-3">Current Page: {{ currentPage }}</p>

    </div>
</template>
<script>


import maskMixins from '@/helpers/mixins/mask'

export default {

        mixins:[maskMixins], 

        data () {
          return {
            perPage: 3,
            currentPage: 1,
            fornecedores:[],
            columns:[  
                      {
                        key:'index',
                        label:'Indice',
                        formatter: 'index'
                      },
                      {
                        key: 'nome',
                        sortable: true
                       // formatter: 'todasMaiusculas'
                      },
                       {
                        key: 'valor',
                        sortable: true
                      },
                       {
                        key: 'quantidade',
                        sortable: true
                      },
                      {
                        key: 'fornecedor.nome',
                        label:'Fornecedor',
                        sortable: true
                      },
                      {
                        key: 'opcao',
                        label:'Opções',
                        sortable: true
                      },
          ],
         
            formdata:{
              fornecedor:null
            },
           // modalShow:false,
          //  editedIndex: -1
          }
        },
       mounted() {
                    this.ActionFind(),
                    this.ActionFindFornecedor()

                  },
        computed:{
                  rows() {
                  return this.columns.length
                  },
                          
        },
        methods: {

                  createItem() {
                       // this.modalShow = true;
                        this.formdata = {}
                        this.formdata.fornecedor=null
                        this.fornecedores =this.tableDataFornecedor
                       // this.editedIndex = -1;
                    }

          },
  }
</script>
<style >

</style>